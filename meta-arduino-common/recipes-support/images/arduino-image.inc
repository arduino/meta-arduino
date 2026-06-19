# Setup image features
IMAGE_FEATURES:append = " ssh-server-dropbear"

# Install root devicetree and devicetree overlays
CORE_IMAGE_EXTRA_INSTALL += " \
    arduino-device-tree \
"

# Install custom u-boot scripts (mandatory for eMMC / SD Card boot)
CORE_IMAGE_EXTRA_INSTALL += " \
    u-boot-script-arduino \
"

# Install extra packages (minimal)
CORE_IMAGE_EXTRA_INSTALL += " \
    packagegroup-core-full-cmdline-extended \
    procps \
    iw \
    net-tools \
    iproute2 \
    wpa-supplicant \
    dnsmasq \
    i2c-tools \
    fio \
    stress-ng \
    bluez5 \
    libgpiod \
    libgpiod-tools \
    can-utils \
    u-boot-fw-utils \
    ethtool \
    usbutils \
    sudo \
"

# Install extra packages (extra), comment if too big
CORE_IMAGE_EXTRA_INSTALL += " \
    packagegroup-base \
    systemd \
    networkmanager \
    networkmanager-wifi \
    networkmanager-nmcli \
    iperf3 \
    openocd \
    htop \
    hdparm \
    dtc \
    python3-core \
    python3-smbus2 \
    python3-periphery \
    python3-pyserial \
    android-tools \
    android-tools-adbd \
    fb-test \
    fbida \
    v4l-utils \
    strace \
"

# Change users and passwords
# echo -n "password" | openssl passwd -6 -stdin
# escape any $ character
inherit extrausers
EXTRA_USERS_PARAMS = "usermod -p '' root;"

ARDUINO-USER = "arduino"
ARDUINO-UID = "1001"
ARDUINO-GID = "1001"

# Create arduino user and add to groups
EXTRA_USERS_PARAMS += " \
    groupadd -g ${ARDUINO-GID} ${ARDUINO-USER}; \
    useradd -u ${ARDUINO-UID} -g ${ARDUINO-GID} -p '' -G audio,sudo,users,plugdev -d /home/${ARDUINO-USER} -r -s /bin/bash ${ARDUINO-USER}; \
"

# Create home directory for arduino user and set permissions
create_arduino_home() {
    mkdir -p ${IMAGE_ROOTFS}/home/${ARDUINO-USER}
    chown ${ARDUINO-UID}:${ARDUINO-GID} ${IMAGE_ROOTFS}/home/${ARDUINO-USER}
}

ROOTFS_POSTPROCESS_COMMAND += "create_arduino_home; "

fakeroot add_custom_sudoers () {
    # Enable sudo group for arduino user
    install -d -m 0755 ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d
    printf '%%sudo\tALL=(ALL:ALL) ALL\n' > ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/sudo-group
    chmod 0440 ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/sudo-group
}

IMAGE_PREPROCESS_COMMAND += "add_custom_sudoers; "

# Custom task to write git SHA to /etc/os-release
write_git_sha() {
    META_LAYER_DIR="${TOPDIR}/../sources/meta-arduino"
    if [ -d "${META_LAYER_DIR}/.git" ] && command -v git >/dev/null 2>&1; then
        GIT_SHA=$(cd ${META_LAYER_DIR} && git rev-parse HEAD 2>/dev/null || echo "unknown")
        REMOTE=$(cd ${META_LAYER_DIR} && git remote | head -n 1 || echo "origin")
        GIT_REMOTE=$(cd ${META_LAYER_DIR} && git config --get remote.${REMOTE}.url 2>/dev/null || echo "unknown")
        echo "META_LAYER_GIT_SHA=${GIT_SHA}" >> ${IMAGE_ROOTFS}/etc/os-release
        echo "META_LAYER_GIT_REMOTE=${GIT_REMOTE}" >> ${IMAGE_ROOTFS}/etc/os-release
    fi

    MANIFEST_DIR="${TOPDIR}/../.repo/manifests"
    if [ -d "${MANIFEST_DIR}/.git" ] && command -v git >/dev/null 2>&1; then
        GIT_SHA=$(cd ${MANIFEST_DIR} && git rev-parse HEAD 2>/dev/null || echo "unknown")
        REMOTE=$(cd ${MANIFEST_DIR} && git remote | head -n 1 || echo "origin")
        GIT_REMOTE=$(cd ${MANIFEST_DIR} && git config --get remote.${REMOTE}.url 2>/dev/null || echo "unknown")
        echo "MANIFEST_GIT_SHA=${GIT_SHA}" >> ${IMAGE_ROOTFS}/etc/os-release
        echo "MANIFEST_GIT_REMOTE=${GIT_REMOTE}" >> ${IMAGE_ROOTFS}/etc/os-release
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "write_git_sha; "

# Late rootfs tweaks
late_rootfs_tweaks() {
    # Remove interfaces from init-ifupdown
    if [ -f "${IMAGE_ROOTFS}/etc/network/interfaces" ]; then
        rm ${IMAGE_ROOTFS}/etc/network/interfaces
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "late_rootfs_tweaks; "