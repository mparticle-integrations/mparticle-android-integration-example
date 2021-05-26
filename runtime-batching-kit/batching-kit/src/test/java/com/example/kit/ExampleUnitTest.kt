package com.example.kit

import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
       val events = BatchingKit().getMPEvents(JSONObject(batchSample))
assertEquals(1, events.size);
    }


    val batchSample =
        """
            {
              "echo": true,
              "dt": "h",
              "id": "f0db2be9-acfe-42f6-af12-43f458b2616b",
              "ct": 1622054741126,
              "sdk": "5.17.0",
              "oo": false,
              "uitl": 10,
              "stl": 60,
              "mpid": "-4997115836606348647",
              "dbg": true,
              "das": "9b392482-ae75-40d6-a855-c668ea3be336",
              "ck": {},
              "con": {
                "gdpr": {}
              },
              "ctx": {
                "dpln": {
                  "id": "dataplan1",
                  "v": 1
                }
              },
              "msgs": [
                {
                  "dt": "fr",
                  "ct": 1622054740337,
                  "dct": "offline",
                  "sid": "3244DCCA-0DD0-48D0-BF42-27968540904D",
                  "sct": 1622054740337,
                  "cs": {
                    "fds": 5587558400,
                    "efds": 5587558400,
                    "amt": 7286000,
                    "ama": 3341776,
                    "amm": 536870912,
                    "sma": 1046626304,
                    "tsm": 2091237376,
                    "bl": 1,
                    "tss": 2384,
                    "gps": true,
                    "dct": "offline",
                    "so": 1,
                    "sbo": 1,
                    "sml": false,
                    "smt": 150994944
                  },
                  "id": "ad8e0f6c-0576-4f58-b676-2b907d6a60a5"
                },
                {
                  "dt": "ss",
                  "ct": 1622054740337,
                  "id": "3244DCCA-0DD0-48D0-BF42-27968540904D",
                  "cs": {
                    "fds": 5587558400,
                    "efds": 5587558400,
                    "amt": 7286000,
                    "ama": 3186040,
                    "amm": 536870912,
                    "sma": 1046118400,
                    "tsm": 2091237376,
                    "bl": 1,
                    "tss": 2487,
                    "gps": true,
                    "dct": "offline",
                    "so": 1,
                    "sbo": 1,
                    "sml": false,
                    "smt": 150994944
                  }
                },
                {
                  "dt": "ast",
                  "ct": 1622054740357,
                  "sid": "3244DCCA-0DD0-48D0-BF42-27968540904D",
                  "sct": 1622054740337,
                  "t": "app_init",
                  "nsi": 0,
                  "ifr": true,
                  "iu": false,
                  "cs": {
                    "fds": 5587558400,
                    "efds": 5587558400,
                    "amt": 7286000,
                    "ama": 2874568,
                    "amm": 536870912,
                    "sma": 1045983232,
                    "tsm": 2091237376,
                    "bl": 1,
                    "tss": 2714,
                    "gps": true,
                    "dct": "offline",
                    "so": 1,
                    "sbo": 1,
                    "sml": false,
                    "smt": 150994944
                  },
                  "id": "de84812c-2f9c-48eb-9b3c-e97526689d1b"
                },
                {
                  "dt": "e",
                  "et": "Search",
                  "n": "p",
                  "el": 43490,
                  "attrs": {
                    "            Category            ": "xbAj1501bfs4ujzo,Csp",
                    "EventLength": "43490"
                  },
                  "flags": {
                    "ACbdhpgxurckg6aAnivrnj etc48B20Coahjyw,l6k3elB": [
                      "strygdCx"
                    ]
                  },
                  "ct": 1622054740376,
                  "sid": "3244DCCA-0DD0-48D0-BF42-27968540904D",
                  "sct": 1622054740337,
                  "est": 1622054740376,
                  "en": 0,
                  "cs": {
                    "fds": 5587558400,
                    "efds": 5587558400,
                    "amt": 7286000,
                    "ama": 2858152,
                    "amm": 536870912,
                    "sma": 1045991424,
                    "tsm": 2091237376,
                    "bl": 1,
                    "tss": 2810,
                    "gps": true,
                    "dct": "offline",
                    "so": 1,
                    "sbo": 1,
                    "sml": false,
                    "smt": 150994944
                  },
                  "id": "787f8ff6-a292-414c-a768-a0063b577db7"
                },
                {
                  "dt": "e",
                  "et": "UserContent",
                  "n": "b",
                  "el": 94827,
                  "attrs": {
                    "nv1boAiwm4czcAjBvex": "j,g6wx,7mnC3pylumB5b1e57jCii90ysjrkn4bA7bt1lrn3t",
                    "rivp8n8 0l8z 52pbto15dfqsy2c5esq80,jq5jcqsb vjsyA6Av": "6Cee ywbfxg7hwx2cmmd8zg5dcv6incjs69gv4opAowbwjb",
                    "0B2ylfAo,04dzcByzdzc90": "42u",
                    "7kzyc61dA0lu,m,089x9xg j": null,
                    "52fig756mb,ld5v,n9stm2980Ce8174f2tob57tyh,dAxi5zk0kn": "jc4sA89ri2",
                    "srm9qv0m0": "Cxuwrs5 zfwncmt ulo4,dmCoiawv5almvqeo1ewhk,e,",
                    "            Category            ": "hB68na49r9p",
                    "EventLength": "94827"
                  },
                  "flags": {
                    "Bj78l6,wwbqkoeooe30wiwr8ee1cAnt5kqduq0s363hn4dplh8": [
                      "uCp8ookayzb402A38m6h8Ajk08"
                    ],
                    "ybrxi0ystlh6z9ccki9wok65hccklky7sr": [
                      "bm8n0usqmdwb18d 9BB53yba92v,yv520zukgB045tbithgxdfuz"
                    ]
                  },
                  "ct": 1622054740541,
                  "sid": "3244DCCA-0DD0-48D0-BF42-27968540904D",
                  "sct": 1622054740337,
                  "est": 1622054740541,
                  "en": 1,
                  "cs": {
                    "fds": 5587558400,
                    "efds": 5587558400,
                    "amt": 7286000,
                    "ama": 2841736,
                    "amm": 536870912,
                    "sma": 1045995520,
                    "tsm": 2091237376,
                    "bl": 1,
                    "tss": 2899,
                    "gps": true,
                    "dct": "offline",
                    "so": 1,
                    "sbo": 1,
                    "sml": false,
                    "smt": 150994944
                  },
                  "id": "ec217d10-8c6b-437b-90ee-4c719d0aef3f"
                },
                {
                  "dt": "e",
                  "et": "UserPreference",
                  "n": "ug",
                  "el": 1,
                  "attrs": {
                    "EventLength": "1"
                  },
                  "ct": 1622054740561,
                  "sid": "3244DCCA-0DD0-48D0-BF42-27968540904D",
                  "sct": 1622054740337,
                  "est": 1622054740561,
                  "en": 2,
                  "cs": {
                    "fds": 5587558400,
                    "efds": 5587558400,
                    "amt": 7286000,
                    "ama": 2825320,
                    "amm": 536870912,
                    "sma": 1046003712,
                    "tsm": 2091237376,
                    "bl": 1,
                    "tss": 2991,
                    "gps": true,
                    "dct": "offline",
                    "so": 1,
                    "sbo": 1,
                    "sml": false,
                    "smt": 150994944
                  },
                  "id": "e116569f-3540-45f4-bb09-47887e7327ba"
                }
              ],
              "ai": {
                "apn": "com.mparticle.test",
                "abn": "0",
                "an": "com.mparticle.test",
                "bid": "cfcd2084-95d5-35ef-a6e7-dff9f98764da",
                "dbg": true,
                "pir": false,
                "ict": 1622054740542,
                "lc": 1,
                "lud": 0,
                "lcu": 1,
                "ud": 1622054740542,
                "env": 1,
                "fi": true
              },
              "di": {
                "bid": "QSR1.190920.001",
                "b": "google",
                "p": "sdk_gphone_x86",
                "dn": "generic_x86",
                "dma": "Google",
                "dp": "Android",
                "dosv": "29",
                "dosvi": 29,
                "dmdl": "Android SDK built for x86",
                "vr": "10",
                "duid": "a8a13c0da5303c64",
                "anid": "a8a13c0da5303c64",
                "ouid": "a8a13c0da5303c64",
                "dbe": false,
                "dbv": "none",
                "dsnfc": false,
                "dst": true,
                "jb": {
                  "cydia": false
                },
                "dsh": 1794,
                "dsw": 1080,
                "dpi": 420,
                "dc": "United States",
                "dlc": "US",
                "dll": "en",
                "tzn": "EST",
                "tz": -5,
                "nca": "Android",
                "nc": "us",
                "mcc": "310",
                "mnc": "260",
                "it": false,
                "idst": true,
                "se": false,
                "ve": false
              },
              "ui": [],
              "ua": {}
            }
        """.trimIndent()
}