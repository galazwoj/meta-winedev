FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI:append_hikey960 = " \
    file://xorg.conf.d/42-fbdev.conf \
"

do_install:append_hikey960 () {
        install -d ${D}/${datadir}/X11/xorg.conf.d/
        install -m 0644 ${WORKDIR}/xorg.conf.d/42-fbdev.conf ${D}/${datadir}/X11/xorg.conf.d/
}

FILES_${PN}_hikey960 += "${datadir}/X11/xorg.conf.d/*"
