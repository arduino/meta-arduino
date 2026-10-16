SUMMARY = "Arduino App CLI command line tool"
DESCRIPTION = "arduino-app-cli is a command line tool running on Arduino UNO Q boards that manages and runs Arduino Apps, provides a HTTP daemon mode to expose RestFul APIs, and auto-updates itself and other components. Includes preinstalled Arduino App examples."
HOMEPAGE = "https://github.com/arduino/arduino-app-cli"
LICENSE = "GPL-3.0-or-later & MPL-2.0 & MIT & OFL-1.1 & CC0-1.0"
LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/GPL-3.0-or-later;md5=1c76c4cc354acaac30ed4d5eefea7245 \
    file://${EXAMPLES_SRC_DIR}/LICENSES/MPL-2.0.txt;md5=48a3fe23ed1353e0995dadfda05ffdb6 \
    file://${EXAMPLES_SRC_DIR}/LICENSES/MIT.txt;md5=3912d958d00bac4a6b550f75d7c806bb \
    file://${EXAMPLES_SRC_DIR}/LICENSES/OFL-1.1.txt;md5=2680fce30f17e5fed9bcebd9336e5b78 \
    file://${EXAMPLES_SRC_DIR}/LICENSES/CC0-1.0.txt;md5=65d3616852dbf7b1a6d4b53b00626032 \
"

PV = "0.13.0"
EXAMPLE_VERSION = "0.13.1"

SRC_URI = " \
    git://github.com/arduino/arduino-app-cli.git;nobranch=1;protocol=https;name=cli \
    git://github.com/arduino/app-bricks-examples.git;nobranch=1;protocol=https;name=examples;tag=${EXAMPLE_VERSION};destsuffix=app-bricks-examples \
"
SRCREV_cli = "v${PV}"
SRCREV_examples = "5d32cd504c0ebbebfdd104aa75fd938b1ef2dec1"
SRCREV_FORMAT = "cli_examples"

EXAMPLES_SRC_DIR = "${S}/../app-bricks-examples"

inherit go

BBCLASSEXTEND = "native"

GO_INSTALL = "${GO} build -trimpath -o arduino-app-cli ./cmd/arduino-app-cli/main.go"

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

    # Match the layout installed by upstream build-deb:clone-examples.
    install -d ${D}${localstatedir}/lib/arduino-app-cli/examples
    for dir in core-and-foundational inspirational bricks; do
        cp -R ${EXAMPLES_SRC_DIR}/$dir \
            ${D}${localstatedir}/lib/arduino-app-cli/examples/
    done
    install -m 0644 ${EXAMPLES_SRC_DIR}/examples.json \
        ${D}${localstatedir}/lib/arduino-app-cli/examples/examples.json
}

FILES:${PN} += "${localstatedir}/lib/arduino-app-cli/examples"

RDEPENDS:${PN} += " \
    arduino-router \
    arduino-cli \
    openocd \
"

# Not strictly necessary for now
#RDEPENDS:${PN} += " arduino-cloud-connector"
