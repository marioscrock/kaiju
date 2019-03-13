FROM fluent/fluentd:v1.3.2-debian-onbuild
RUN buildDeps="sudo make gcc g++ libc-dev ruby-dev" \
 && apt-get update \
 && apt-get install -y --no-install-recommends $buildDeps \
 && sudo gem install fluent-plugin-kubernetes_metadata_filter -v 2.0.0 \
 && sudo gem install fluent-plugin-systemd -v 1.0.1 \
 && sudo gem install oj -v 3.5.1 \
 && sudo gem install fluent-plugin-multi-format-parser -v 1.0.0 \
 && sudo gem install fluent-plugin-concat -v 2.3.0 \
 && sudo gem install fluent-plugin-rewrite-tag-filter -v 2.1.0 \
 && sudo gem install fluent-plugin-prometheus -v 1.0.0 \
 && sudo gem sources --clear-all \
 && SUDO_FORCE_REMOVE=yes \
    apt-get purge -y --auto-remove \
                  -o APT::AutoRemove::RecommendsImportant=false \
                  $buildDeps \
 && rm -rf /var/lib/apt/lists/* \
           /home/fluent/.gem/ruby/2.3.0/cache/*.gem
