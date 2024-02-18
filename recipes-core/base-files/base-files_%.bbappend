FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

do_install:append() {
    # use customized .bashrc template
    install -m 0755 ${WORKDIR}/share/dot.bashrc ${D}${sysconfdir}/skel/.bashrc
    # install .bashrc copy into root user home directory
    install -m 0755 ${WORKDIR}/share/dot.bashrc ${D}${ROOT_HOME}/.bashrc
    # install .profile copy into root user home directory so .bashrc is sourced by login shells
    install -m 0755 ${WORKDIR}/share/dot.profile ${D}${ROOT_HOME}/.profile
}
