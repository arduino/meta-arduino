FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://0001-usb_ffs-fix-bcdVersion-for-os_desc_header.patch \
"
