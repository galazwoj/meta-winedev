# defaultsetup.inc gets includes after ${DISTRO}.conf, so we need to set it here
# to make the python below work. Local, site and auto.conf will override it.
TCMODE ?= "default"

# Enable multilib conditionally, only for aarch64 with default toolchain combination
# Other combinations aren't supported
def get_multilib_handler(d):
    features = d.getVar('TUNE_FEATURES', True).split()
    tcmode = d.getVar('TCMODE', True)
    if ('aarch64' in features) and (tcmode == "default"):
        distro_multilib = "conf/distro/include/distro-multilib-aarch64.inc"
    else:
        distro_multilib = "conf/distro/include/file-cannot-be-found.inc"
    return distro_multilib

# Use a weak include to avoid to produce an error when the file cannot be found.
# It is the case when we don't want multilib enabled (e.g. on 32bit machines).
include ${@get_multilib_handler(d)}
