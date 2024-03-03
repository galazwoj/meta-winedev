DESCRIPTION = "ssh private & public key packages"

# To avoid QA Issue: ssh-keys: Recipe file fetches files and does not have
# license file information (LIC_FILES_CHKSUM) [license-checksum]
LICENSE = "CLOSED"

inherit allarch

# You are expected to create user own ssh keys on the host and copy them under 'files'.
# Example: ssh-keygen -t rsa -b 4096 -N "" -f ~/.ssh/winedev
SSH_KEY_NAME = "winedev"

SRC_URI = "file://${SSH_KEY_NAME} \
           file://${SSH_KEY_NAME}.pub \
          "

S = "${WORKDIR}"

do_install() {
    install -m 0700 -d ${D}/home/${USER}/.ssh/
    install -m 0700 ${S}/${SSH_KEY_NAME} ${D}/home/${USER}/.ssh/id_rsa
    install -m 0755 ${S}/${SSH_KEY_NAME}.pub ${D}/home/${USER}/.ssh/id_rsa.pub
    install -m 0600 ${S}/${SSH_KEY_NAME}.pub ${D}/home/${USER}/.ssh/authorized_keys
}

PACKAGES += "${PN}-client ${PN}-server"

FILES:${PN}-client += "/home/${USER}/.ssh/id_rsa.pub /home/${USER}/.ssh/id_rsa"
FILES:${PN}-server += "/home/${USER}/.ssh/authorized_keys"

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_build[noexec] = "1"
