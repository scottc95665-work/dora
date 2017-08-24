FROM docker.elastic.co/elasticsearch/elasticsearch:5.5.1
ADD  elasticsearch.yml /usr/share/elasticsearch/config/
USER root
RUN bin/elasticsearch-plugin install analysis-phonetic
RUN chown elasticsearch:elasticsearch config/elasticsearch.yml
USER elasticsearch