FROM docker.elastic.co/elasticsearch/elasticsearch:6.4.1
ADD elasticsearch.yml /usr/share/elasticsearch/config/
ADD jvm.options /usr/share/elasticsearch/config/
ADD names_synonyms.txt /usr/share/elasticsearch/config/
USER root
RUN bin/elasticsearch-plugin install analysis-phonetic
ADD x-pack-perry-realm.zip /usr/share/elasticsearch/
RUN bin/x-pack/extension install file:/usr/share/elasticsearch/x-pack-perry-realm.zip
RUN chown elasticsearch:elasticsearch config/elasticsearch.yml
RUN chown -R elasticsearch:elasticsearch /usr/share/elasticsearch/plugins
USER elasticsearch
