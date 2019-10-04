FROM pgbouncer/pgbouncer:latest

WORKDIR /opt/pgbouncer
COPY files/entrypoint.sh /opt/pgbouncer/entrypoint.sh
RUN chmod 755 /opt/pgbouncer/entrypoint.sh

ENTRYPOINT ["/bin/project"]

