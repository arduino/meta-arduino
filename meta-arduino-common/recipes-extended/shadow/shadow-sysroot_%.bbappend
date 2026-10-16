# Reserve UID/GID 1000 for the Arduino user by raising the floor used for
# automatic UID/GID allocation (e.g. by recipes using --user-group, like
# weston-init). Any account created without an explicit -u/-g now starts
# from 1001, so it no longer collides with the Arduino user/group (1000).
do_install:append() {
    sed -i \
        -e 's/^UID_MIN[[:space:]]\+[0-9]\+/UID_MIN\t\t\t 1001/' \
        -e 's/^GID_MIN[[:space:]]\+[0-9]\+/GID_MIN\t\t\t 1001/' \
        ${D}${sysconfdir}/login.defs
}
