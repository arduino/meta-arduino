# Additions for the arduino partner image

FILESEXTRAPATHS:prepend := "${THISDIR}/configs:"

IMAGE_FEATURES:remove = " ssh-server-openssh"
IMAGE_FEATURES += "ssh-server-dropbear"

SUDOERS_FILE ?= "sudoers-arduino-lmp"

SRC_URI += "\
    file://${SUDOERS_FILE} \
"

LMP_EXTRA = " \
    lmp-device-tree \
    lmp-auto-hostname \
    compose-apps-early-start \
    tmate \
    libgpiod-tools \
"

ARDUINO = " \
    arduino-ootb \
    m4-proxy \
    udev-rules-portenta \
    libusbgx-config \
"

CORE_IMAGE_BASE_INSTALL += " \
    libdrm \
    usb-modeswitch \
    ${LMP_EXTRA} \
    ${ARDUINO} \
"

# Packages to be installed in Portenta-X8 machine only
IMAGE_INSTALL:append:portenta-x8 = " openocd"

USERADD_UID_TABLES:lmp="files/arduino-passwd-table"
USERADD_GID_TABLES:lmp="files/arduino-group-table"

# No default passwords are configured for the device (PSTI Act 2022)
EXTRA_USERS_PARAMS += "\
    usermod -p '' root; \
    usermod -p '' ${LMP_USER}; \
"

# Custom task to write git SHA to /etc/os-release
write_git_sha() {
    META_LAYER_DIR="${OEROOT}/layers/meta-arduino"
    GIT_SHA=$(cd ${META_LAYER_DIR} && git rev-parse HEAD)
    echo "META_LAYER_GIT_SHA=${GIT_SHA}" >> ${IMAGE_ROOTFS}/etc/os-release
}

ROOTFS_POSTPROCESS_COMMAND += "write_git_sha"

fakeroot do_populate_rootfs_add_custom_sudoers () {
    # Allow sudo group users to use sudo
    bbwarn Changing sudoers for arduino ootb!
    install -m 0440 ${WORKDIR}/${SUDOERS_FILE} ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/51-arduino
}

IMAGE_PREPROCESS_COMMAND += "do_populate_rootfs_add_custom_sudoers"

IMAGE_FSTYPES:append = " wic.zst tar.zst"
