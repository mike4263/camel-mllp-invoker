package com.redhat.naps.launch;

import jakarta.enterprise.context.ApplicationScoped;

import java.text.SimpleDateFormat;
import java.util.Date;

@ApplicationScoped
public class RouteBuilder extends org.apache.camel.builder.RouteBuilder {

    static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    static String hl7MessageTemplate
            = "MSH|^~\\&|REQUESTING|ICE|INHOUSE|RTH00|<MESSAGE_TIMESTAMP>||ORM^O01|<MESSAGE_CONTROL_ID>|D|2.3|||AL|NE|||" + '\r'
            + "PID|1||ICE999999^^^ICE^ICE||Testpatient^Testy^^^Mr||19740401|M|||123 Barrel Drive^^^^SW18 4RT|||||2||||||||||||||"
            + '\r'
            + "NTE|1||Free text for entering clinical details|" + '\r'
            + "PV1|1||^^^^^^^^Admin Location|||||||||||||||NHS|" + '\r'
            + "ORC|NW|213||175|REQ||||20080808093202|ahsl^^Administrator||G999999^TestDoctor^GPtests^^^^^^NAT|^^^^^^^^Admin Location | 819600|200808080932||RTH00||ahsl^^Administrator||"
            + '\r'
            + "OBR|1|213||CCOR^Serum Cortisol ^ JRH06|||200808080932||0.100||||||^|G999999^TestDoctor^GPtests^^^^^^NAT|819600|ADM162||||||820|||^^^^^R||||||||"
            + '\r'
            + "OBR|2|213||GCU^Serum Copper ^ JRH06 |||200808080932||0.100||||||^|G999999^TestDoctor^GPtests^^^^^^NAT|819600|ADM162||||||820|||^^^^^R||||||||"
            + '\r'
            + "OBR|3|213||THYG^Serum Thyroglobulin ^JRH06|||200808080932||0.100||||||^|G999999^TestDoctor^GPtests^^^^^^NAT|819600|ADM162||||||820|||^^^^^R||||||||"
            + '\r'
            + '\n';

    public static String getHL7Message() {
        String tmpMessage = hl7MessageTemplate.replaceFirst("<MESSAGE_TIMESTAMP>", timestampFormat.format(new Date()));
        return tmpMessage.replaceFirst("<MESSAGE_CONTROL_ID>", String.format("%05d", 1));
    }

    @Override
    public void configure() throws Exception {
        from("timer:send-mllp?delay=-1&repeatCount=1")
                .routeId("FromTimer2MLLP")
                .setBody(simple(getHL7Message()))
                .to("log:before?showAll=true&multiline=true")
                .to("mllp://{{mllp.ip}}:{{mllp.port}}")
                .log("Message sent via MLLP to {{mllp.ip}}:{{mllp.port}}")
                .log("Received Type - ${header.CamelMllpAcknowledgementType}")
                .log("Received - ${header.CamelMllpAcknowledgementString}")
                .to("log:after?showAll=true&multiline=true");
    }
}
