SUMMARY = "Arduino CLI command line tool"
DESCRIPTION = "Arduino CLI is the command line tool for Arduino development. This recipe builds it for both target and native."
HOMEPAGE = "https://github.com/arduino/arduino-cli"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0-only;md5=c79ff39f19dfec6d293b95dea7b07891"

PV = "1.4.1"

SRC_URI = "git://github.com/arduino/arduino-cli.git;branch=master;protocol=https"
SRCREV = "v${PV}"

inherit go

BBCLASSEXTEND = "native"

GO_INSTALL = "${GO} build -trimpath -o arduino-cli ./main.go"

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
    install -m 0755 ${S}/arduino-cli ${D}${bindir}/arduino-cli
}
