(defproject graphhopper-cljc "0.1.0-SNAPSHOT"
  :description "develop graphhopper with clj"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ;; dependencies extracted from graphhoppers poml.xml files
                 [com.carrotsearch/hppc "0.8.1"]
                 [org.apache.xmlgraphics/xmlgraphics-commons "2.3"]
                 [commons-io/commons-io "1.3.1"]
                 [junit/junit "4.12"]
                 [org.locationtech.jts/jts-core "1.15.1"]
                 [org.slf4j/slf4j-api "1.7.26"]
                 [com.fasterxml.jackson.core/jackson-core "2.9.9"]
                 [com.graphhopper.external/jackson-datatype-jts "0.12-2.5-1"]
                 [io.dropwizard/dropwizard-testing "1.3.12"
                   :exclusions [org.glassfish.jersey.core/jersey-server
                                org.glassfish.hk2/hk2-utils]]
                 [org.openstreetmap.osmosis/osmosis-osm-binary "0.47"]
                 [org.apache.commons/commons-compress "1.18"]]
  :java-source-paths ["api/src" "web-api/src" "core/src" "reader-osm/src" "graphhopper-osm-id-mapping/src"]
  :source-paths ["clj-dev/src"]
  :resource-paths ["core/src/main/resources"]
  :main ^:skip-aot graphhopper-cljc.core
  :profiles {:dev {:repl-options {:init-ns graphhopper-cljc.core}}
             :uberjar {:aot :all}})
