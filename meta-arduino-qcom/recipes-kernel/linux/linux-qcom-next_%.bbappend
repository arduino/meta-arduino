FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:imola = " file://imola.cfg"
SRC_URI:append:monza = " file://monza.cfg \
    file://0001-misc-fastrpc-fix-double-free-in-fastrpc_map_attach.patch \
    file://0002-misc-fastrpc-remove-dead-context-drain-in-fastrpc_user_free.patch \
    file://0003-misc-fastrpc-drop-the-DSP-response-reference-on-channel-removal.patch \
    file://0004-misc-fastrpc-fix-context-leak-and-hang-on-signal-interrupted-invoke.patch \
"
