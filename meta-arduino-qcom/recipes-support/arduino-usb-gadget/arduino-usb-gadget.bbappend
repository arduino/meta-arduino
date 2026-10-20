FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:monza = " file://arduino-gadget-setup.machine"
SRC_URI:append:imola = " file://arduino-gadget-setup.machine"

do_install:append() {
    if [ -f ${UNPACKDIR}/arduino-gadget-setup.machine ]; then
        install -d ${D}${sysconfdir}
        install -m 0644 ${UNPACKDIR}/arduino-gadget-setup.machine ${D}${sysconfdir}/arduino-gadget-setup.machine
    fi
}
