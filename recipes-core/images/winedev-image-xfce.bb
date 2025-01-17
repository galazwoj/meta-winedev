require recipes-core/images/core-image-minimal-xfce.bb

DESCRIPTION = "Winedev image with minimal XFCE install"
LICENSE = "MIT"

IMAGE_BASENAME = "${PN}"
DISTRO_FEATURES += "x11" 
DISTRO_FEATURES += "x11 pam"
DISTRO_FEATURES += "ptest"
DISTRO_FEATURES += "opengl"

#REQUIRED_DISTRO_FEATURES = "x11"
#REQUIRED_DISTRO_FEATURES += "pam"

IMAGE_FEATURES += " \
    debug-tweaks \
    dev-pkgs \
    dbg-pkgs \
    tools-sdk \
    tools-debug \
    eclipse-debug \
    tools-profile \
    tools-testapps \
    ssh-server-openssh \
    "

IMAGE_LINGUAS += " en-us en-gb de-de fr-fr ru-ru ja-jp"

# Add 1GB extra space to rootfs
IMAGE_ROOTFS_EXTRA_SPACE += " +1000000"

SDKIMAGE_FEATURES += " \
    dev-pkgs \
    dbg-pkgs \
    doc-pkgs \
    staticdev-pkgs \
    tools-sdk \
    tools-debug \
    multiarch \
    "

IMAGE_INSTALL += " \
    git \
    kernel-devsrc \
    sudo \
    sshfs-fuse \
    ssh-keys-client \
    ssh-keys-server \
    usbutils \
    ltrace \
    glmark2 \
    xscreensaver \
    "

# for multilib SDK (32-bit + 64-bit) targeting Wine devel
IMAGE_INSTALL += " \
    ${@multilib_pkg_extend(d, "alsa-lib")} \
    ${@multilib_pkg_extend(d, "cups")} \
    ${@multilib_pkg_extend(d, "fontconfig")} \
    ${@multilib_pkg_extend(d, "freetype")} \
    ${@multilib_pkg_extend(d, "gstreamer1.0-meta-base")} \
    ${@multilib_pkg_extend(d, "lcms")} \
    ${@multilib_pkg_extend(d, "libglu")} \
    ${@multilib_pkg_extend(d, "libgphoto2")} \
    ${@multilib_pkg_extend(d, "libkrb5")} \
    ${@multilib_pkg_extend(d, "libpcap")} \
    ${@multilib_pkg_extend(d, "libpng")} \
    ${@multilib_pkg_extend(d, "libsdl2")} \
    ${@multilib_pkg_extend(d, "libv4l")} \
    ${@multilib_pkg_extend(d, "libx11")} \
    ${@multilib_pkg_extend(d, "libxcursor")} \
    ${@multilib_pkg_extend(d, "libxinerama")} \
    ${@multilib_pkg_extend(d, "libxcomposite")} \
    ${@multilib_pkg_extend(d, "libxml2")} \
    ${@multilib_pkg_extend(d, "libxslt")} \
    ${@multilib_pkg_extend(d, "mpg123")} \
    ${@multilib_pkg_extend(d, "openldap")} \
    ${@multilib_pkg_extend(d, "openal-soft")} \
    ${@multilib_pkg_extend(d, "pulseaudio")} \
    ${@multilib_pkg_extend(d, "tiff")} \
    ${@multilib_pkg_extend(d, "vulkan-loader")} \
    "

# User accounts customizations
inherit extrausers

# Reset root password to 'root'
# See https://docs.yoctoproject.org/singleindex.html#extrausers-bbclass
# We set a default password of root to match our busybox instance setup
# Don't do this in a production image
# ROOT_PASSWD below is set to the output of
# printf "%q" $(mkpasswd -m sha256crypt root) to hash the "root" password
# string
ROOT_PASSWD = "\$5\$2WoxjAdaC2\$l4aj6Is.EWkD72Vt.byhM5qRtF9HcCM/5YpbxpmvNB5"
EXTRA_USERS_PARAMS += "usermod -p '${ROOT_PASSWD}' root;"

# Add current host user to the target system
# USER_PASSWD below is set to the output of
# printf "%q" $(mkpasswd -m sha256crypt mypass) to hash the "$USER" password
# string
USER_PASSWD = "\$5\$oy3x6RC2DyjCks86$pIQ6y3k5GTyVbmkNHqIel8Qjf2MTX/ReN6b8ZayQ482"
EXTRA_USERS_PARAMS += "\
    useradd -p '${USER_PASSWD}' ${USER}; \
    "
# Provide user account sudoer privileges by default
EXTRA_USERS_PARAMS += "\
    usermod -a -G sudo ${USER}; \
    "
# On graphical images, add user to video group
EXTRA_USERS_PARAMS += "\
    usermod -a -G video ${USER}; \
    "

# uncomment the line %sudo ALL=(ALL) ALL in /etc/sudoers
modify_sudoers() {
    sed 's/# %sudo/%sudo/' < ${IMAGE_ROOTFS}${sysconfdir}/sudoers > \
        ${IMAGE_ROOTFS}${sysconfdir}/sudoers.tmp
    mv ${IMAGE_ROOTFS}${sysconfdir}/sudoers.tmp ${IMAGE_ROOTFS}${sysconfdir}/sudoers
}
ROOTFS_POSTPROCESS_COMMAND:append = " modify_sudoers;"

# copy custom .bashrc and .profile to user $HOME folder
copy_user_home_bashrc() {
    install -m 0755 ${IMAGE_ROOTFS}${sysconfdir}/skel/.bashrc ${IMAGE_ROOTFS}/home/${USER}
    # .profile to make .bashrc being sourced by login shells
    install -m 0755 ${IMAGE_ROOTFS}${sysconfdir}/skel/.profile ${IMAGE_ROOTFS}/home/${USER}
}
ROOTFS_POSTPROCESS_COMMAND:append = " copy_user_home_bashrc;"

# Fix ownership of new user $HOME folder
modify_user_home_ownership() {
    chown -R ${USER}:${USER} ${IMAGE_ROOTFS}/home/${USER}
}
ROOTFS_POSTPROCESS_COMMAND:append = " modify_user_home_ownership;"
