(ns graphhopper-cljc.core
  (:gen-class)
  (:import [java.util Locale]
           [com.graphhopper GraphHopper GHRequest GHResponse PathWrapper]
           [com.graphhopper.util Translation InstructionList]
           [com.graphhopper.reader.osm GraphHopperOSM]
           [com.graphhopper.routing.util EncodingManager]
           [org.apache.commons.compress.compressors.bzip2 BZip2CompressorInputStream]
           [com.graphhopper.storage GraphHopperStorage Graph NodeAccess]
           [com.graphhopper.routing.util AllEdgesIterator EdgeFilter]
           [com.graphhopper.storage.index LocationIndex QueryResult]
           [com.graphhopper.osmidexample MyGraphHopper]))


(def osmFile "core/files/berlin-siegessaeule.osm.gz")
(def graphFolder "target/graph/berlin-siegessaeule")
;(def osmFile "data/niedersachsen-latest.osm.bz2")
     ;; minlat="52.5130300" minlon="13.3476600" maxlat="52.5157400" maxlon="13.3527600"
;(def graphFolder "target/graph/niedersachsen")
(def encoding "car")

(def hopper (some-> (MyGraphHopper.) #_(GraphHopperOSM.)
                    .forServer
                    (.setDataReaderFile osmFile)
                    (.setGraphHopperLocation graphFolder)
                    (.setEncodingManager (EncodingManager/create encoding))
                    .importOrLoad))
(instance? GraphHopper hopper)


(def locale Locale/GERMAN)
(def tr (-> hopper .getTranslationMap (.getWithFallBack locale)))
(instance? Translation tr)


(def vehicle "car")
(def weighting "fastest")

(def req (let [[latFrom lonFrom] [52.5156643 13.3515446] #_[52.3358 9.8280]
               [latTo lonTo] [52.5152721 13.3511300] #_[52.3957 9.5375]]
              (some-> (GHRequest. latFrom lonFrom latTo lonTo)
                      (.setVehicle vehicle)
                      (.setWeighting weighting)
                      (.setLocale locale))))
(instance? GHRequest req)


(def rsp (.route hopper req))
(instance? GHResponse rsp)

(if (.hasErrors rsp)
    (.getErrors rsp))

(when-not (.hasErrors rsp)
  (def path (.getBest rsp))
  (or (instance? PathWrapper path) (nil? path))
  
  {:meters (.getDistance path)
   :seconds (/ (.getTime path) 1000.0)}
  
  (def il (.getInstructions path))
  (instance? InstructionList il)
  (map (fn [instruction] {:description (.getTurnDescription instruction tr)
                          :extra (.getExtraInfoJSON instruction)})
       il))

(def path (some-> (.calcPaths hopper req rsp)
                  first))
(def edge (first (.calcEdges path)))
(def edgeId (.getEdge edge))
(.getOSMWay hopper edgeId)


(def ghStorage (.getGraphHopperStorage hopper))
(instance? GraphHopperStorage ghStorage)

(def bg (.getBaseGraph ghStorage))
(instance? Graph bg)

(def edges (.getAllEdges bg))
(instance? AllEdgesIterator edges)
(.length edges)
(.getName edges)
(.getDistance edges)
;(def edgeId (.getEdge edges))
(def nodeId (.getBaseNode edges))

(def nodeAccess (.getNodeAccess bg))
(instance? NodeAccess nodeAccess)
(.getLat nodeAccess nodeId)

(def locationIndex (.getLocationIndex hopper))
(instance? LocationIndex locationIndex)
(def qr (.findClosest locationIndex 52.35 9.6 EdgeFilter/ALL_EDGES))
(instance? QueryResult qr)
(.getClosestNode qr)
(.getClosestEdge qr)

(defn -main
  "example"
  [& args]
  (println il))
