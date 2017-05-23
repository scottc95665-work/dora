FROM docker.elastic.co/elasticsearch/elasticsearch:5.3.2
ADD  elasticsearch.yml /usr/share/elasticsearch/config/
RUN chown elasticsearch:elasticsearch config/elasticsearch.yml
USER elasticsearch