FROM node:18.20.2-bookworm

USER root

# Update and upgrade package manager
RUN apt update && apt-get update

# Install Angular CLI globally and other dependencies
RUN npm install -g @angular/cli

# Create appuser with uid 10000
RUN adduser --disabled-password --shell /sbin/nologin --uid 10000 --home /opt/app  appuser  
RUN sed -i -r 's/^appuser:!:/appuser:x:/' /etc/shadow

# Remove existing crontabs, if any
RUN rm -Rf /var/spool/cron /etc/crontabs /etc/periodic

# Remove unnecessary user accounts.
RUN sed -i -r '/^(appuser|root|sshd)/!d' /etc/group
RUN sed -i -r '/^(appuser|root|sshd)/!d' /etc/passwd
#RUN cat /etc/passwd
#RUN cat /etc/group

# Remove interactive login shell for all users except appuser.
RUN sed -i -r '/^appuser:/! s#^(.*):[^:]*$#\1:/sbin/nologin#' /etc/passwd

# Set sysdirs for further cleanup
ENV sysdirs="\
  /bin \
  /etc \
  /lib \
  /sbin \
  /usr \
  /usr/bin \
"
# Ensure system dirs are owned by root and not writable by anybody else
RUN find $sysdirs -xdev -type d \
  -exec chown root:root {} \; \
  -exec chmod 0755 {} \;

# Remove all suid files.
RUN find $sysdirs -xdev -type f -a -perm /4000 -delete
# Remove some unused admin commands.
#RUN ls -a
RUN find /sbin/* ! -type f \
  -a ! -name nologin \
  -a ! -name setup-proxy \
  -a ! -name sshd \
  -a ! -name start.sh \
  -a ! -name apt \
  -exec rm -rv {} \;

RUN find /usr/sbin/* ! -type f \
  -a ! -name nologin \
  -a ! -name setup-proxy \
  -a ! -name sshd \
  -a ! -name start.sh \
  -a ! -name apt \
  -exec rm -rv {} \;  

# Remove some of the programs that is not required and could be dangerous
RUN find $sysdirs -xdev \( \
  -name hexdump -o \
  -name chgrp -o \
  -name od -o \
  -name strings -o \
  -name su -o \
  -name ping -o \
  -name netstat -o \
  -name gunzip -o \
  -name nslookup \
  \) -delete

# Remove init scripts since we do not use them.
RUN rm -Rf /etc/init.d /lib/rc /etc/conf.d /etc/inittab /etc/runlevels /etc/rc.conf

# Remove kernel tunables since we do not need them.
RUN rm -Rf /etc/sysctl* /etc/modprobe.d /etc/modules /etc/mdev.conf /etc/acpi /root /etc/fstab

# Remove broken symlinks
RUN find $sysdirs -xdev -type l -exec test ! -e {} \; -delete

# Remove unwanted Cache & Packages 
RUN rm -rf /var/cache/apt/*
  
# # Switch to appuser
# USER appuser

# Set working directory
WORKDIR /opt/app

# Copy package.json and package-lock.json (if available)
COPY package*.json ./

# Install all project dependencies
RUN npm ci

# Copy the rest of your app's source code
COPY . ./

# # Ensure correct permissions
# RUN chown -R appuser:appuser /opt/app

# Expose the port your app runs on
EXPOSE 4200

# Command to run your app using Angular CLI
CMD ["ng", "serve", "--host", "0.0.0.0"]

