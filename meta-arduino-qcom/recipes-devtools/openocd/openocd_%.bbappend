FILESEXTRAPATHS:prepend:imola := "${THISDIR}/files/imola:"

# imola: build from the facchinm fork (jomla branch)
SRC_URI:imola = " \
    git://github.com/facchinm/OpenOCD.git;protocol=https;name=openocd;branch=jomla;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX} \
    git://github.com/msteveb/jimtcl.git;protocol=https;name=jimtcl;branch=master;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/jimtcl \
    file://openocd_gpiod.cfg \
    file://stm32u5x.cfg \
    file://stm32x5x_common.cfg \
    file://bin/arduino-debug.sh \
    file://bin/arduino-flash.sh \
    file://bin/arduino-reset.sh \
    file://share/openocd/scripts/mem_helper.tcl \
    file://share/openocd/scripts/target/swj-dp.tcl \
"

SRCREV_openocd:imola = "e6a2c12f41c9d1131b980c47af7517b4a5aca110"
SRCREV_jimtcl:imola = "f160866171457474f7c4d6ccda70f9b77524407e"

DEPENDS:append:imola = " libgpiod"

EXTRA_OECONF:append:imola = " --enable-linuxgpiod --enable-internal-jimtcl"
PACKAGECONFIG:remove:imola = " sysfsgpio"

OPENOCD_INSTALL_DIR = "/opt/openocd"

do_install:append:imola() {
    # Install cfg files
    install -d ${D}${OPENOCD_INSTALL_DIR}
    install -m 0644 ${UNPACKDIR}/openocd_gpiod.cfg      ${D}${OPENOCD_INSTALL_DIR}/
    install -m 0644 ${UNPACKDIR}/stm32u5x.cfg            ${D}${OPENOCD_INSTALL_DIR}/
    install -m 0644 ${UNPACKDIR}/stm32x5x_common.cfg     ${D}${OPENOCD_INSTALL_DIR}/

    # Install Arduino helper scripts
    install -d ${D}${OPENOCD_INSTALL_DIR}/bin
    install -m 0755 ${UNPACKDIR}/bin/arduino-debug.sh    ${D}${OPENOCD_INSTALL_DIR}/bin/
    install -m 0755 ${UNPACKDIR}/bin/arduino-flash.sh    ${D}${OPENOCD_INSTALL_DIR}/bin/
    install -m 0755 ${UNPACKDIR}/bin/arduino-reset.sh    ${D}${OPENOCD_INSTALL_DIR}/bin/

    # Install custom TCL scripts directly under OPENOCD_INSTALL_DIR so openocd
    # can find them via "-s ${INSTALL_PATH}" (i.e. -s /opt/openocd)
    install -d ${D}${OPENOCD_INSTALL_DIR}/target
    install -m 0644 ${UNPACKDIR}/share/openocd/scripts/mem_helper.tcl \
        ${D}${OPENOCD_INSTALL_DIR}/
    install -m 0644 ${UNPACKDIR}/share/openocd/scripts/target/swj-dp.tcl \
        ${D}${OPENOCD_INSTALL_DIR}/target/

    # Symlink the openocd binary into /opt/openocd/bin so the scripts work
    ln -sf ${bindir}/openocd ${D}${OPENOCD_INSTALL_DIR}/bin/openocd
}

FILES:${PN}:append:imola = " ${OPENOCD_INSTALL_DIR}/"
