FROM pgbouncer/pgbouncer:latest

COPY files/entrypoint.sh .
RUN chmod 755 ./entrypoint.sh

ENTRYPOINT ["/bin/project"]

