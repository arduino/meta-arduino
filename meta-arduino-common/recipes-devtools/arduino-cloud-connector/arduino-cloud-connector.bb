SUMMARY = "Arduino Cloud Connector daemon"
DESCRIPTION = "Arduino IoT Cloud connector daemon for Linux boards"
HOMEPAGE = "https://github.com/arduino/arduino-cloud-connector"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

PV = "0.1.0"

SRC_URI = "git://github.com/arduino/arduino-cloud-connector.git;nobranch=1;protocol=https"
SRCREV = "v${PV}"

inherit go systemd

GO_INSTALL = "${GO} build -trimpath -o arduino-cloud-connector ./cmd/arduino-cloud-connector/main.go"

SYSTEMD_SERVICE:${PN} = "arduino-cloud-connector.service"
SYSTEMD_AUTO_ENABLE = "enable"

RDEPENDS:${PN} += "ca-certificates"

# Keep go module cache writable for rm_work cleanup.
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
    install -m 0755 ${S}/arduino-cloud-connector ${D}${bindir}/arduino-cloud-connector

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/debian/arduino-cloud-connector/etc/systemd/system/arduino-cloud-connector.service \
        ${D}${systemd_system_unitdir}/

    install -d ${D}/usr/local/share/ca-certificates
    install -m 0644 ${S}/debian/arduino-cloud-connector/usr/local/share/ca-certificates/arduino_prod.crt \
        ${D}/usr/local/share/ca-certificates/
    install -m 0644 ${S}/debian/arduino-cloud-connector/usr/local/share/ca-certificates/arduino_staging.crt \
        ${D}/usr/local/share/ca-certificates/
}

pkg_postinst:${PN}() {
#!/bin/sh
if [ -n "$D" ]; then
    exit 0
fi

# Match upstream package behavior: ensure data dir exists and is private.
install -d /home/arduino/.local/share/arduino-cloud-connector
chown -R arduino:arduino /home/arduino/.local/share/arduino-cloud-connector || true
chmod -R 700 /home/arduino/.local/share/arduino-cloud-connector

update-ca-certificates --fresh || true
}

FILES:${PN} += " \
    ${systemd_system_unitdir}/arduino-cloud-connector.service \
    /usr/local/share/ca-certificates/arduino_prod.crt \
    /usr/local/share/ca-certificates/arduino_staging.crt \
"
