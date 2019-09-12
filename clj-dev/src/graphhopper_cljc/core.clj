(ns graphhopper-cljc.core
  (:import [java.util Locale]
           [com.graphhopper GraphHopper GHRequest GHResponse PathWrapper]
           [com.graphhopper.util Translation InstructionList]
           [com.graphhopper.reader.osm GraphHopperOSM]
           [com.graphhopper.routing.util EncodingManager]))


(def osmFile "core/files/berlin-siegessaeule.osm.gz")
     ;; minlat="52.5130300" minlon="13.3476600" maxlat="52.5157400" maxlon="13.3527600"
(def graphFolder "target/graph")
(def encoding "car")

(def hopper (some-> (GraphHopperOSM.)
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

(def req (let [[latFrom lonFrom] [52.5156643 13.3515446]
               [latTo lonTo] [52.5152721 13.3511300]]
              (some-> (GHRequest. latFrom lonFrom latTo lonTo)
                      (.setVehicle vehicle)
                      (.setWeighting weighting)
                      (.setLocale locale))))
(instance? GHRequest req)


(def rsp (.route hopper req))
(instance? GHResponse rsp)

(if (.hasErrors rsp)
    (.getErrors rsp))

(def path (.getBest rsp))
(or (instance? PathWrapper path) (nil? path))

{:meters (.getDistance path)
 :seconds (/ (.getTime path) 1000.0)}

(def il (.getInstructions path))
(instance? InstructionList il)
(map (fn [instruction] {:description (.getTurnDescription instruction tr)
                        :extra (.getExtraInfoJSON instruction)})
     il)
