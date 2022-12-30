package swagger;

import requests.RestService;
import templates.Definition;
import templates.ExternalDocs;
import templates.Info;
import templates.Path;
import templates.Swagger;
import templates.Tag;
import utils.Routes;

public class SwaggerService {
	private RestService restService;
	private Routes routes;
	private String[] schemes;
	private String host;
	private String basePath;
	
	public SwaggerService(String[] schemes, String host, String basePath, String aasIdShort) {
		this.restService = new RestService();
		this.routes = new Routes(restService, schemes[0]+"://"+host, aasIdShort);
		this.schemes = schemes;
		this.host = host;
		this.basePath = basePath;
	}
	
	
	public void generateDocumentation() {
		Info info = Data.generateInfo(restService, routes);
		Tag[] tags = Data.generateTags();
		Path[] paths = Data.generatePaths(restService, routes);
		Definition[] definitions = Data.generateDefinitions(restService, routes);
		ExternalDocs externalDocs = Data.generateExternalDocs(restService, routes);
		Swagger swagger = new Swagger("2.0", info, host, basePath, tags, schemes, paths, definitions, externalDocs);
		//TODO: fix classCastException
			//Swagger.java
			//Transform.arrayToJson((String[]) tagList.toArray())
			//problem: array cast?
		System.out.println(swagger.asJson().toString());
	}
}
