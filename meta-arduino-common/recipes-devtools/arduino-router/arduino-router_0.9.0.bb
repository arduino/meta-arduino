SUMMARY = "Arduino Router - MessagePack RPC Router service"
DESCRIPTION = "MessagePack RPC Router that allows RPC calls between multiple \
MessagePack RPC clients connected in a star topology network, where the Router \
is the central node."
HOMEPAGE = "https://github.com/arduino/arduino-router"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1ebbd3e34237af26da5dc08a4e440464"

PV = "0.9.0"

SRC_URI = "git://github.com/arduino/arduino-router.git;protocol=https;nobranch=1"
SRCREV = "e25c513d74a0105c020b677a1c9b8228618fb586"

S = "${UNPACKDIR}/git"

inherit go systemd

do_compile() {
    cd ${S}
    export CGO_ENABLED=0
    ${GO} build -trimpath -o arduino-router .
    ${GO} build -trimpath -o arduino-router-cli ./cmd/arduino-router-cli
}

do_rm_work:prepend() {
    bbwarn "Fixing permissions into ${WORKDIR}/build/pkg/mod"
    chmod -R u+w ${WORKDIR}/build/pkg/mod
}

DEBIAN_DIR = "${S}/debian/arduino-router"

SYSTEMD_SERVICE:${PN} = " \
    arduino-router.service \
    arduino-router-serial.path \
"
SYSTEMD_AUTO_ENABLE = "disable"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/arduino-router ${D}${bindir}/arduino-router
    install -m 0755 ${S}/arduino-router-cli ${D}${bindir}/arduino-router-cli

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${DEBIAN_DIR}/etc/systemd/system/arduino-router.service \
        ${D}${systemd_system_unitdir}/
    install -m 0644 ${DEBIAN_DIR}/etc/systemd/system/arduino-router-serial.service \
        ${D}${systemd_system_unitdir}/
    install -m 0644 ${DEBIAN_DIR}/etc/systemd/system/arduino-router-serial.path \
        ${D}${systemd_system_unitdir}/

    install -d ${D}${libdir}/systemd/system-generators
    install -m 0755 \
        ${DEBIAN_DIR}/usr/lib/systemd/system-generators/systemd-arduino-router.sh \
        ${D}${libdir}/systemd/system-generators/systemd-arduino-router

    install -d ${D}/var/lib/arduino-router/config
    install -m 0644 ${DEBIAN_DIR}/var/lib/arduino-router/config/10-imola.conf \
        ${D}/var/lib/arduino-router/config/
    install -m 0644 ${DEBIAN_DIR}/var/lib/arduino-router/config/10-monza.conf \
        ${D}/var/lib/arduino-router/config/
}

RDEPENDS:${PN} += "socat"

FILES:${PN} += " \
    ${libdir}/systemd/system-generators/systemd-arduino-router \
    /var/lib/arduino-router/ \
"
