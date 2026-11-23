# Setup image features
IMAGE_FEATURES:append = " ssh-server-dropbear"
IMAGE_FEATURES:append = " enable-adbd"

# Necessary to actually start android-tools-adbd, not just install it.
inherit image-adbd

# Install root devicetree and devicetree overlays
CORE_IMAGE_EXTRA_INSTALL += " \
    arduino-device-tree \
"

# Install custom u-boot scripts (mandatory for eMMC / SD Card boot)
CORE_IMAGE_EXTRA_INSTALL += " \
    u-boot-script-arduino \
"

# USB composite gadget (acm / ffs-adb / ncm), driven by /etc/default/usbgx.
# Also ships a drop-in making android-tools-adbd (when installed) attach to the
# ffs.adb function created here instead of composing its own gadget.
CORE_IMAGE_EXTRA_INSTALL += " \
    arduino-usb-gadget \
    libusbgx-config \
"

# Expand rootfs filesystem on first boot to fit media storage
CORE_IMAGE_EXTRA_INSTALL += " \
    expand-rootfs \
"

# Install extra packages (minimal)
CORE_IMAGE_EXTRA_INSTALL += " \
    packagegroup-arduino-bringup \
"

# NetworkManager (from packagegroup-arduino-bringup) manages the network.
# Drop systemd-networkd, recommended by systemd: it manages no interface here,
# and its systemd-networkd-wait-online times out after 2 minutes on every boot.
BAD_RECOMMENDATIONS += "systemd-networkd"

# Install extra packages (extra), comment if too big
CORE_IMAGE_EXTRA_INSTALL += " \
    packagegroup-arduino-bringup-extra \
"

# Privilege escalation and user administration
CORE_IMAGE_EXTRA_INSTALL += " \
    sudo \
"

# Change users and passwords
# echo -n "password" | openssl passwd -6 -stdin
# escape any $ character
inherit extrausers
EXTRA_USERS_PARAMS = "usermod -p '' root;"

#TODO FIX ARDUINO GROUP: the arduino group is now created by the arduino-app-cli recipe
#ARDUINO-GID = "1001"
#EXTRA_USERS_PARAMS += " \
#    groupadd -g ${ARDUINO-GID} arduino; \
#    ${@bb.utils.contains('IMAGE_FEATURES', 'enable-adbd', 'usermod -a -G arduino adb;', '', d)} \
#"

fakeroot add_custom_sudoers () {
    # Enable sudo group
    install -d -m 0755 ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d
    printf '%%sudo\tALL=(ALL:ALL) ALL\n' > ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/sudo-group
    chmod 0440 ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/sudo-group
}

IMAGE_PREPROCESS_COMMAND += "add_custom_sudoers"
