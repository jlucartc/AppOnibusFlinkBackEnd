import java.util.Base64

val coordsBytes = "\"payload_raw\":\"OC4wMDAwOjcuNjU1NQ==\"".split(':')(1).replaceAllLiterally("\"","").getBytes()

val coordsStr  = new String(Base64.getDecoder.decode(coordsBytes)).replaceAllLiterally(" ","")

coordsStr