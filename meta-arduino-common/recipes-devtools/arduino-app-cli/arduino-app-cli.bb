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

# Group owning the arduino-app-cli data directory (see arduino-app-assets)
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system arduino"

# The native binary is used by arduino-app-assets to download the assets
BBCLASSEXTEND = "native"

GO_INSTALL = "${GO} build -trimpath -tags immutableos -o arduino-app-cli ./cmd/arduino-app-cli/main.go"

# This recipe has no vendor/ dir, so go build fetches missing modules from
# GOPROXY live during do_compile; allow network access for that task since
# bitbake blocks it by default.
do_compile[network] = "1"

do_rm_work:prepend() {
    if [ -d "${WORKDIR}/build/pkg/mod" ]; then
        bbwarn "Fixing permissions into ${WORKDIR}/build/pkg/mod"
        chmod -R u+w ${WORKDIR}/build/pkg/mod
    fi
}

# Override do_compile to use GO_INSTALL
python do_compile() {
    import os
    bb.build.exec_func('go_do_compile', d)
}

go_do_compile() {
    cd ${S}
    export CGO_ENABLED=0
    ${GO_INSTALL}
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/arduino-app-cli ${D}${bindir}/arduino-app-cli
}

do_install:append:class-target() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/arduino-app-cli.service ${D}${systemd_system_unitdir}/
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
