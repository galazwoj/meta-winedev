# .bashrc

umask 022

# Source global definitions
if [ -f /etc/bashrc ]; then
	. /etc/bashrc
fi

# User specific environment
if ! [[ "$PATH" =~ "$HOME/.local/bin:$HOME/bin:" ]]
then
    PATH="$HOME/.local/bin:$HOME/bin:$PATH"
fi
# allow non-root users/sudoers to use sbin as well
if ! [[ "$PATH" =~ "/usr/local/sbin:/usr/sbin:/sbin" ]]
then
    PATH="$PATH:/usr/local/sbin:/usr/sbin:/sbin"
fi
export PATH

# Set a colored prompt if the terminal supports it
case "$TERM" in
    xterm-color|*-256color)
        PS1="\[\e[1;36m\]\u@\h\[\e[0m\]:\[\e[1;34m\]\W\[\e[0m\]"
        COLOR_OPTION="--color=auto"
        ;;
    *)
        PS1="\u@\h:\W"
        ;;
esac

# Set prompt sign according to the current user
if [ "`id -u`" -eq 0 ]; then
    PS1="$PS1# "
else
    PS1="$PS1$ "
fi

# You may uncomment the following lines if you want `ls' to be colorized:
export LS_OPTIONS='--color=auto'
eval `dircolors`
alias ls='ls $LS_OPTIONS'
alias ll='ls $LS_OPTIONS -l'
alias l='ls $LS_OPTIONS -lA'

# Some more alias to avoid making mistakes:
alias rm='rm -i'
alias cp='cp -i'
alias mv='mv -i'
