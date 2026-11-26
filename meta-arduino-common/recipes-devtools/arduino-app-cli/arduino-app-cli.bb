SUMMARY = "Arduino App CLI command line tool"
DESCRIPTION = "arduino-app-cli is a command line tool running on Arduino UNO Q boards that manages and runs Arduino Apps, provides a HTTP daemon mode to expose RestFul APIs, and auto-updates itself and other components."

inherit arduino-app-cli-core
inherit go useradd systemd

FILESEXTRAPATHS:prepend := "${THISDIR}:"
SRC_URI += "file://arduino-app-cli.service"

# Separate package for the systemd service, so the daemon can be pulled in
# (or left out) independently of the arduino-app-cli binary/library package.
PACKAGES =+ "${PN}-daemon"

SUMMARY:${PN}-daemon = "Arduino App CLI daemon systemd service"
RDEPENDS:${PN}-daemon = "${PN}"

SYSTEMD_PACKAGES = "${PN}-daemon"
SYSTEMD_SERVICE:${PN}-daemon = "arduino-app-cli.service"
SYSTEMD_AUTO_ENABLE:${PN}-daemon = "enable"

FILES:${PN}-daemon += "${systemd_system_unitdir}/arduino-app-cli.service"

# Separate packages for the static assets and examples (downloaded at build
# time with the native arduino-app-cli), so either can be installed/left out
# independently of the CLI binary and of each other.
PACKAGES =+ "${PN}-assets ${PN}-examples"

SUMMARY:${PN}-assets = "Arduino App CLI runner assets"
DESCRIPTION:${PN}-assets = "Static assets (docs, compose templates, ...) installed in the arduino-app-cli data directory."
RDEPENDS:${PN}-assets = "${PN}"

SUMMARY:${PN}-examples = "Arduino App CLI examples"
DESCRIPTION:${PN}-examples = "Example Arduino Apps installed in the arduino-app-cli data directory."
RDEPENDS:${PN}-examples = "${PN}"

ARDUINO_APP_CLI_DATA_DIR = "/var/lib/arduino-app-cli"

ASSETS_DIR = "${WORKDIR}/assets"
# download-examples creates an examples/ folder inside its destination
EXAMPLES_DEST = "${WORKDIR}/examples-download"
EXAMPLES_DIR = "${EXAMPLES_DEST}/examples"

FILES:${PN}-assets = "${ARDUINO_APP_CLI_DATA_DIR}/assets"
FILES:${PN}-examples = "${ARDUINO_APP_CLI_DATA_DIR}/examples"
FILES:${PN} += "${ARDUINO_APP_CLI_DATA_DIR}"

# Group owning the arduino-app-cli data directory (see the -assets/-examples
# packages above)
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system arduino"

# The native binary is used to download the assets/examples bundled in the
# -assets/-examples packages
DEPENDS:append:class-target = " arduino-app-cli-native"

BBCLASSEXTEND = "native"

GO_INSTALL = "${GO} build -trimpath -tags immutableos -o arduino-app-cli ./cmd/arduino-app-cli/main.go"

# This recipe has no vendor/ dir, so go build fetches missing modules from
# GOPROXY live during do_compile; allow network access for that task since
# bitbake blocks it by default.
do_compile[network] = "1"
do_compile[cleandirs] += " ${ASSETS_DIR} ${EXAMPLES_DEST}"

do_rm_work:prepend() {
    if [ -d "${WORKDIR}/build/pkg/mod" ]; then
        bbwarn "Fixing permissions into ${WORKDIR}/build/pkg/mod"
        chmod -R u+w ${WORKDIR}/build/pkg/mod
    fi
}

# Override do_compile to use GO_INSTALL; the target variant additionally
# downloads the runner assets and examples with the native CLI.
python do_compile() {
    bb.build.exec_func('go_do_compile', d)
    if not bb.data.inherits_class('native', d):
        bb.build.exec_func('download_app_cli_assets', d)
}

go_do_compile() {
    cd ${S}
    export CGO_ENABLED=0
    ${GO_INSTALL}
}

download_app_cli_assets() {
    # The CLI creates its folders at startup (and refuses to run as root
    # without ALLOW_ROOT): keep all of them inside WORKDIR, not in the build
    # host's /var/lib and $HOME.
    export HOME=${WORKDIR}/home
    export ARDUINO_APP_CLI__DATA_DIR=${WORKDIR}/data
    export ARDUINO_APP_CLI__APPS_DIR=${WORKDIR}/apps
    export ARDUINO_APP_BRICKS__CUSTOM_MODEL_DIR=${WORKDIR}/custom-models
    export MODELS_PATH=${WORKDIR}/models
    export ARDUINO_APP_CLI__ALLOW_ROOT=true
    install -d ${HOME}

    ${STAGING_BINDIR_NATIVE}/arduino-app-cli system download-runner-assets --dest ${ASSETS_DIR}
    ${STAGING_BINDIR_NATIVE}/arduino-app-cli system download-examples --dest ${EXAMPLES_DEST}
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/arduino-app-cli ${D}${bindir}/arduino-app-cli
}

do_install:append:class-target() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/arduino-app-cli.service ${D}${systemd_system_unitdir}/

    install -d ${D}${ARDUINO_APP_CLI_DATA_DIR}
    cp -R --no-preserve=ownership ${ASSETS_DIR} ${D}${ARDUINO_APP_CLI_DATA_DIR}/
    cp -R --no-preserve=ownership ${EXAMPLES_DIR} ${D}${ARDUINO_APP_CLI_DATA_DIR}/

    # 775 root:arduino with the setgid bit, so new files inherit the group
    chown root:arduino ${D}${ARDUINO_APP_CLI_DATA_DIR}
    chmod 2775 ${D}${ARDUINO_APP_CLI_DATA_DIR}
    chown -R root:arduino ${D}${ARDUINO_APP_CLI_DATA_DIR}/assets
    find ${D}${ARDUINO_APP_CLI_DATA_DIR}/assets -type d -exec chmod 2775 {} +
    find ${D}${ARDUINO_APP_CLI_DATA_DIR}/assets -type f -exec chmod u=rwX,g=rwX,o=rX {} +

    chown -R root:arduino ${D}${ARDUINO_APP_CLI_DATA_DIR}/examples
    find ${D}${ARDUINO_APP_CLI_DATA_DIR}/examples -type d -exec chmod 2775 {} +
    find ${D}${ARDUINO_APP_CLI_DATA_DIR}/examples -type f -exec chmod u=rwX,g=rwX,o=rX {} +
}

# Runtime deps only apply to the target build: the native variant has no
# arduino-router-native/arduino-cli-native counterparts.
RDEPENDS:${PN}:append:class-target = " \
    arduino-router \
    arduino-cli \
    openocd \
"

# Not strictly necessary for now
#RDEPENDS:${PN} += " arduino-cloud-connector"
