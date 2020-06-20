DESCRIPTION = "linaro.org john.stultz Linux kernel for HiKey960"
SECTION = "kernel"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit kernel siteinfo

# Specify the commandline for your device

# Set the verbosity of kernel messages during runtime
# You can define CMDLINE_DEBUG in your local.conf or distro.conf to override this behaviour
CMDLINE_DEBUG ?= '${@oe.utils.conditional("DISTRO_TYPE", "release", "quiet", "debug", d)}'
CMDLINE_append = " ${CMDLINE_DEBUG} "

ALLOW_EMPTY_${KERNEL_PACKAGE_NAME}-devicetree = "1"

python __anonymous () {

    import bb

    devicetree = d.getVar('KERNEL_DEVICETREE', True) or ''
    if devicetree:
        depends = d.getVar("DEPENDS", True)
        d.setVar("DEPENDS", "%s dtc-native" % depends)
}

# dev/hikey960-4.20 branch
PV = "4.20+git${SRCPV}"
SRCREV_kernel = "c86e3d9c59ae0d34d4ecc68efa8f26d427a62cce"
SRCREV_FORMAT = "kernel"

SRC_URI = "git://git.linaro.org/people/john.stultz/android-dev.git;protocol=https;name=kernel;branch=dev/hikey960-4.20"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-generic:${THISDIR}/linux-wine:${THISDIR}/linux-crng:"

# dtc fails to build with -fno-common or gcc-10
# https://bugs.gentoo.org/706660
SRC_URI += " \
    file://0001-scripts-dtc-Remove-redundant-YYLOC-global-declaratio.patch \
    "

# Wine bug: https://bugs.winehq.org/show_bug.cgi?id=46132
SRC_URI += " \
    file://0001-arm64-kernel-Enable-PMU-access-from-EL0.patch \
    "

# Mitigate CVE-2018-1108: https://access.redhat.com/security/cve/cve-2018-1108
SRC_URI += " \
    file://0001-random-Also-allow-CONFIG_RANDOM_TRUST_CPU-for-ARM64-.patch \
    file://random-trust-cpu.cfg \
    "

# fix perf builds with newer binutils 2.34+
# http://git.yoctoproject.org/cgit/cgit.cgi/meta-intel/commit/?id=2b0a523adeb5ab8b20135375dccb3cacce582fbe
SRC_URI += " \
    file://perf-fix-build-with-binutils.patch \
    "

KERNEL_CONFIG_FRAGMENTS_append = " \
    ${WORKDIR}/random-trust-cpu.cfg \
    "

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey960"
KERNEL_IMAGETYPE ?= "Image"

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    # Make sure to disable certain items:
    # - Android-related baseline (CONFIG_ANDROI, powermgmt)
    #   The defconfig was derived from AOSP development.
    # - Debug symbols (not needed)
    sed -e '/CONFIG_ANDROID=/d' \
        -e '/CONFIG_PM_AUTOSLEEP=/d' \
        -e '/CONFIG_PM_WAKELOCKS=/d' \
        -e '/CONFIG_DEBUG_INFO=/d' \
        < ${S}/arch/arm64/configs/hikey960_defconfig \
        > ${B}/.config

    echo 'CONFIG_LOCALVERSION=""' >>${B}/.config
    echo '# CONFIG_LOCALVERSION_AUTO is not set' >>${B}/.config

    #
    # Udev quirks
    #

    # Newer versions of udev mandate that sysfs doesn't have deprecated entries
        sed -e /CONFIG_SYSFS_DEPRECATED/d \
            -e /CONFIG_SYSFS_DEPRECATED_V2/d \
            -e /CONFIG_HOTPLUG/d \
            -e /CONFIG_UEVENT_HELPER_PATH/d \
            -e /CONFIG_UNIX/d \
            -e /CONFIG_SYSFS/d \
            -e /CONFIG_PROC_FS/d \
            -e /CONFIG_TMPFS/d \
            -e /CONFIG_INOTIFY_USER/d \
            -e /CONFIG_SIGNALFD/d \
            -e /CONFIG_TMPFS_POSIX_ACL/d \
            -e /CONFIG_BLK_DEV_BSG/d \
            -e /CONFIG_AUTOFS4_FS/d \
            -i '${B}/.config'

        echo '# CONFIG_SYSFS_DEPRECATED is not set' >> ${B}/.config
        echo '# CONFIG_SYSFS_DEPRECATED_V2 is not set' >> ${B}/.config
        echo 'CONFIG_HOTPLUG=y' >> ${B}/.config
        echo 'CONFIG_UEVENT_HELPER_PATH=""' >> ${B}/.config
        echo 'CONFIG_UNIX=y' >> ${B}/.config
        echo 'CONFIG_SYSFS=y' >> ${B}/.config
        echo 'CONFIG_PROC_FS=y' >> ${B}/.config
        echo 'CONFIG_TMPFS=y' >> ${B}/.config
        echo 'CONFIG_INOTIFY_USER=y' >> ${B}/.config
        echo 'CONFIG_SIGNALFD=y' >> ${B}/.config
        echo 'CONFIG_TMPFS_POSIX_ACL=y' >> ${B}/.config
        echo 'CONFIG_BLK_DEV_BSG=y' >> ${B}/.config
        echo 'CONFIG_DEVTMPFS=y' >> ${B}/.config
        echo 'CONFIG_DEVTMPFS_MOUNT=y' >> ${B}/.config
        echo 'CONFIG_AUTOFS4_FS=y' >> ${B}/.config

    # Newer inits like systemd need cgroup support
        sed -e /CONFIG_CGROUP_SCHED/d \
            -e /CONFIG_CGROUPS/d \
            -i '${B}/.config'

        echo 'CONFIG_CGROUP_SCHED=y' >> ${B}/.config
        echo 'CONFIG_CGROUPS=y' >> ${B}/.config
        echo 'CONFIG_CGROUP_NS=y' >> ${B}/.config
        echo 'CONFIG_CGROUP_FREEZER=y' >> ${B}/.config
        echo 'CONFIG_CGROUP_DEVICE=y' >> ${B}/.config
        echo 'CONFIG_CPUSETS=y' >> ${B}/.config
        echo 'CONFIG_PROC_PID_CPUSET=y' >> ${B}/.config
        echo 'CONFIG_CGROUP_CPUACCT=y' >> ${B}/.config
        echo 'CONFIG_RESOURCE_COUNTERS=y' >> ${B}/.config

    #
    # root-over-nfs-over-usb-eth support. Limited, but should cover some cases.
    # Enable this by setting a proper CMDLINE_NFSROOT_USB.
    #
    if [ ! -z "${CMDLINE_NFSROOT_USB}" ]; then
            oenote "Configuring the kernel for root-over-nfs-over-usb-eth with CMDLINE ${CMDLINE_NFSROOT_USB}"
            sed -e '/CONFIG_INET/d' \
                -e '/CONFIG_IP_PNP=/d' \
                -e '/CONFIG_USB_GADGET=/d' \
                -e '/CONFIG_USB_GADGET_SELECTED=/d' \
                -e '/CONFIG_USB_ETH=/d' \
                -e '/CONFIG_NFS_FS=/d' \
                -e '/CONFIG_ROOT_NFS=/d' \
                -e '/CONFIG_CMDLINE=/d' \
                -i ${B}/.config
            echo "CONFIG_INET=y"                     >> ${B}/.config
            echo "CONFIG_IP_PNP=y"                   >> ${B}/.config
            echo "CONFIG_USB_GADGET=y"               >> ${B}/.config
            echo "CONFIG_USB_GADGET_SELECTED=y"      >> ${B}/.config
            echo "CONFIG_USB_ETH=y"                  >> ${B}/.config
            echo "CONFIG_NFS_FS=y"                   >> ${B}/.config
            echo "CONFIG_ROOT_NFS=y"                 >> ${B}/.config
            echo "CONFIG_CMDLINE=\"${CMDLINE_NFSROOT_USB}\"" >> ${B}/.config
    fi

    echo 'CONFIG_FHANDLE=y' >> ${B}/.config

    # Check for kernel config fragments. The assumption is that the config
    # fragment will be specified with the absolute path. For example:
    #   * ${WORKDIR}/config1.cfg
    #   * ${S}/config2.cfg
    # Iterate through the list of configs and make sure that you can find
    # each one. If not then error out.
    # NOTE: If you want to override a configuration that is kept in the kernel
    #       with one from the OE meta data then you should make sure that the
    #       OE meta data version (i.e. ${WORKDIR}/config1.cfg) is listed
    #       after the in-kernel configuration fragment.
    # Check if any config fragments are specified.
    if [ ! -z "${KERNEL_CONFIG_FRAGMENTS}" ]; then
        for f in ${KERNEL_CONFIG_FRAGMENTS}; do
            # Check if the config fragment was copied into the WORKDIR from
            # the OE meta data
            if [ ! -e "$f" ]; then
                echo "Could not find kernel config fragment $f"
                exit 1
            fi
        done

        # Now that all the fragments are located merge them.
        ( cd ${WORKDIR} && ${S}/scripts/kconfig/merge_config.sh -m -r -O ${B} ${B}/.config ${KERNEL_CONFIG_FRAGMENTS} 1>&2 )
    fi

    yes '' | oe_runmake -C ${S} O=${B} oldconfig

    bbplain "Saving defconfig to:\n${B}/defconfig"
    oe_runmake -C ${B} savedefconfig
}

do_deploy_append() {
    cp -a ${B}/defconfig ${DEPLOYDIR}
}

# bitbake.conf only prepends PARALLEL make in tasks called do_compile, which isn't the case for compile_modules
# So explicitly enable it for that in here
EXTRA_OEMAKE = "${PARALLEL_MAKE} "

do_install_append() {
    install -d ${D}/boot
    make -C ${S} O=${B} ARCH=$ARCH dtbs || true
    install -m 0644 ${B}/arch/$ARCH/boot/dts/*.dtb ${D}/boot || true

    rm -f ${D}${KERNEL_SRC_PATH}/arch/*/vdso/vdso*.so
}

PACKAGES =+ "${KERNEL_PACKAGE_NAME}-devicetree-overlays"
FILES_${KERNEL_PACKAGE_NAME}-devicetree-overlays = "/lib/firmware/*.dtbo /lib/firmware/*.dts"
FILES_${KERNEL_PACKAGE_NAME}-devicetree += "/boot/*.dtb"

RDEPENDS_${KERNEL_PACKAGE_NAME}-image_append = " ${KERNEL_PACKAGE_NAME}-devicetree"
RRECOMMENDS_${KERNEL_PACKAGE_NAME}-image_append = " ${KERNEL_PACKAGE_NAME}-devicetree-overlays"

# Automatically depend on lzop/lz4-native if CONFIG_KERNEL_LZO/LZ4 is enabled
python () {
    try:
        defconfig = bb.fetch2.localpath('file://defconfig', d)
    except bb.fetch2.FetchError:
        return

    try:
        configfile = open(defconfig)
    except IOError:
        return

    if 'CONFIG_KERNEL_LZO=y\n' in configfile.readlines():
        depends = d.getVar('DEPENDS', False)
        d.setVar('DEPENDS', depends + ' lzop-native')

    if 'CONFIG_KERNEL_LZ4=y\n' in configfile.readlines():
        depends = d.getVar('DEPENDS', False)
        d.setVar('DEPENDS', depends + ' lz4-native')
}
