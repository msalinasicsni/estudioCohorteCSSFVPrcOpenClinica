<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://openclinica.org/ws/studySubject/v1" xmlns:bean="http://openclinica.org/ws/beans">
   <soapenv:Header/>
   <soapenv:Body>
      <v1:createRequest>
         <v1:studySubject>
            <!--Optional:-->
            <bean:label>${label}</bean:label>
            <!--Optional:-->
            <bean:secondaryLabel>${secondaryLabel}</bean:secondaryLabel>
            <bean:enrollmentDate>${enrollmentDate}</bean:enrollmentDate>
            <bean:subject>
               <!--Optional:-->
               <bean:uniqueIdentifier>${uniqueIdentifier}</bean:uniqueIdentifier>
               <!--Optional:-->
               <bean:gender>${gender}</bean:gender>
               <!--You have a CHOICE of the next 2 items at this level-->
               <bean:dateOfBirth>${dateOfBirth}</bean:dateOfBirth>
               <bean:yearOfBirth>${yearOfBirth}</bean:yearOfBirth>
            </bean:subject>
            <bean:studyRef>
               <bean:identifier>${identifier}</bean:identifier>
               <!--Optional:-->
               <bean:siteRef>
                  <bean:identifier>${siteidentifier}</bean:identifier>
               </bean:siteRef>
            </bean:studyRef>
         </v1:studySubject>
      </v1:createRequest>
   </soapenv:Body>
</soapenv:Envelope>