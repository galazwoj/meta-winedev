FILESEXTRAPATHS:prepend := "${THISDIR}/linux-wine:${THISDIR}/linux-virtio:"

# Wine bug: https://bugs.winehq.org/show_bug.cgi?id=46132
SRC_URI:append_aarch64 = " \
    file://0001-arm64-kernel-Enable-PMU-access-from-EL0.patch \
    "

SRC_URI:append_qemuarm64 = " \
    file://crypto-dev-virtio.cfg \
    file://drm-virtio-gpu.cfg \
    file://virtio-pci.cfg \
    "

KERNEL_CONFIG_FRAGMENTS:append_qemuarm64 = " \
    ${WORKDIR}/crypto-dev-virtio.cfg \
    ${WORKDIR}/drm-virtio-gpu.cfg \
    ${WORKDIR}/virtio-pci.cfg \
    "
