FROM docker.elastic.co/elasticsearch/elasticsearch:6.8.0
ADD elasticsearch.yml /usr/share/elasticsearch/config/
ADD elastic.pass /usr/share/elasticsearch/config/
ADD jvm.options /usr/share/elasticsearch/config/
ADD names_synonyms.txt /usr/share/elasticsearch/config/
USER root
RUN bin/elasticsearch-plugin install analysis-phonetic
ADD x-pack-perry-realm.zip /usr/share/elasticsearch/
RUN bin/elasticsearch-plugin install --batch file:/usr/share/elasticsearch/x-pack-perry-realm.zip
RUN chown elasticsearch:elasticsearch config/elasticsearch.yml
RUN chown -R elasticsearch:elasticsearch /usr/share/elasticsearch/plugins
RUN bin/elasticsearch-keystore create
RUN cat config/elastic.pass | bin/elasticsearch-keystore add --stdin bootstrap.password
USER elasticsearch
