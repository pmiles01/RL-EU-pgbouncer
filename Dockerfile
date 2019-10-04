FROM pgbouncer/pgbouncer:latest

WORKDIR /opt/pgbouncer
COPY files/entrypoint.sh /opt/pgbouncer/entrypoint.sh
USER root
RUN chmod 755 /opt/pgbouncer/entrypoint.sh
USER postgres

ENTRYPOINT ["/opt/pgbouncer/entrypoint.sh"]

