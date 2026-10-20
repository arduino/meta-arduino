# SPDX-License-Identifier: MIT
# Copyright (C) 2026 Arduino S.r.l.

SUMMARY = "Arduino bring-up package groups"
DESCRIPTION = "Packages for board bring-up, diagnostics, and testing on Arduino platforms"
LICENSE = "MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"

PACKAGES = " \
    ${PN} \
    ${PN}-extra \
"

SUMMARY:${PN} = "Arduino bring-up packages (minimal)"
RDEPENDS:${PN} = " \
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
"

SUMMARY:${PN}-extra = "Arduino bring-up packages (extra)"
RDEPENDS:${PN}-extra = " \
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
    python3-speedtest-cli \
    fb-test \
    fbida \
    v4l-utils \
    strace \
"
