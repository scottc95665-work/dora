ARG ELASTICSEARCH_VERSION
FROM docker.elastic.co/elasticsearch/elasticsearch:$ELASTICSEARCH_VERSION
ADD elasticsearch.yml /usr/share/elasticsearch/config/
USER root
RUN bin/elasticsearch-plugin install analysis-phonetic
ADD x-pack-perry-realm.zip /usr/share/elasticsearch/
RUN bin/x-pack/extension install file:/usr/share/elasticsearch/x-pack-perry-realm.zip
RUN chown elasticsearch:elasticsearch config/elasticsearch.yml
RUN chown -R elasticsearch:elasticsearch /usr/share/elasticsearch/plugins
USER elasticsearch
