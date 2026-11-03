SUMMARY = "Arduino App CLI command line tool"
DESCRIPTION = "arduino-app-cli is a command line tool running on Arduino UNO Q boards that manages and runs Arduino Apps, provides a HTTP daemon mode to expose RestFul APIs, and auto-updates itself and other components."
HOMEPAGE = "https://github.com/arduino/arduino-app-cli"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0-or-later;md5=1c76c4cc354acaac30ed4d5eefea7245"

PV = "0.11.1"

SRC_URI = "git://github.com/arduino/arduino-app-cli.git;nobranch=1;protocol=https"
SRCREV = "600152dac08ee7ebfe89c4d53a2a0a685bf83f2a"

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
}

RDEPENDS:${PN} += " \
    arduino-router \
    arduino-cli \
    openocd \
"

# Not strictly necessary for now
#RDEPENDS:${PN} += " arduino-cloud-connector"
