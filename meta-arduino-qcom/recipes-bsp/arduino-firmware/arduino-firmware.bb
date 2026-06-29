SUMMARY = "Install Arduino firmware binaries for the target"
LICENSE = "Apache-2.0 & LGPL-2.1-or-later"

inherit arduino-core

DEPENDS += "arduino-cli-native base-passwd"

# Use ARDUINO_ZEPHYR_LOADER from machine config
FW_SRC_DIR = "${B}/home/arduino/.arduino15/packages/arduino/hardware/zephyr/${ARDUINO_CORE_VERSION}"

do_compile() {
    do_install_arduino_core
    if [ -n "${ARDUINO_SKETCH_LIBS}" ]; then
        bbwarn "Installing Arduino Libraries: ${ARDUINO_SKETCH_LIBS}"
        ${ARDUINO_CLI} lib update-index
        ${ARDUINO_CLI} lib install ${ARDUINO_SKETCH_LIBS}
    fi
    if [ -n "${ARDUINO_SKETCH}" ]; then
        bbwarn "Compiling Arduino Sketch: ${ARDUINO_SKETCH}"
        ${ARDUINO_CLI} compile --fqbn arduino:zephyr:unoq \
            --build-path ${ARDUINO_BUILD_PATH} \
            "${FW_SRC_DIR}/${ARDUINO_SKETCH_PATH}"
    fi
}

do_install() {
    if [ -n "${ARDUINO_CORE}" ]; then
        install -d ${D}${libdir}/firmware/arduino
        install -m 0644 ${FW_SRC_DIR}/firmwares/${ARDUINO_ZEPHYR_LOADER}.bin ${D}${libdir}/firmware/arduino/
        install -m 0644 ${FW_SRC_DIR}/firmwares/${ARDUINO_ZEPHYR_LOADER}.elf ${D}${libdir}/firmware/arduino/
    fi
    if [ -n "${ARDUINO_SKETCH}" ]; then
        install -d ${D}${libdir}/firmware/arduino
        install -m 0644 ${ARDUINO_BUILD_PATH}/${ARDUINO_SKETCH}.bin ${D}${libdir}/firmware/arduino/
        install -m 0644 ${ARDUINO_BUILD_PATH}/${ARDUINO_SKETCH}.elf ${D}${libdir}/firmware/arduino/
    fi
}

FILES:${PN} += "${libdir}/firmware/arduino"

INSANE_SKIP:${PN} += "already-stripped split-strip arch"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
