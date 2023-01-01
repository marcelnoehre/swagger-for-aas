package swagger;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import requests.RestService;
import templates.Definition;
import templates.Property;
import templates.Request;
import templates.Response;
import templates.Route;
import templates.ExternalDocs;
import templates.Info;
import templates.Items;
import templates.Parameter;
import templates.Path;
import templates.Tag;
import utils.Checks;
import utils.Constants;
import utils.Routes;

public class Data {
	private static Parameter[] generateParameters(Route route) {
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		String[] possibleParameters = new String[] {"{aas.idShort}", "{submodel.idShort}", "{element.idShort}", "{cd.idShort}"};
		for(String possibleParameter : possibleParameters) {
			if(route.getRoute().contains(possibleParameter)) {
				switch(possibleParameter.replace("{", "").replace("}", "")) {
					case "aas.idShort":
						parameterList.add(Constants.AAS_ID_SHORT);
						break;
					case "submodel.idShort":
						parameterList.add(Constants.SUBMODEL_ID_SHORT);
						break;
					case "element.idShort":
						parameterList.add(Constants.ELEMENT_ID_SHORT);
						break;
					case "cd.idShort":
						parameterList.add(Constants.CD_ID_SHORT);
						break;
				}
			}
		}
		if(route.getType().equals("put")) {
			parameterList.add(new Parameter(route.getTag(), "body", "The new or updated " + route.getTag(), "true", null, null, null, null, null, null, null));
		} else if(route.getType().equals("get")) {
			if(route.getExtraParameter() != null) {
				parameterList.add(new Parameter("informationScope", "path", "Defines which information is displayed", "false", "array", null, null, null, new Items("string", route.getExtraParameter(), "", null), "single", null));
			}
		}
		Parameter[] parameters = new Parameter[parameterList.size()];
		int i = 0;
		for(Parameter parameter : parameterList) {
			parameters[i] = parameter;
			i++;
		}
		return parameters;
	}
	
	private static Response[] generateResponses(RestService restService, Routes routes, Route route) {
		String[] good = null;
		String[] bad = null;
		String[][] parameter = new String[][] {new String[] {"idShort", "example"}, new String[] {"id", "example"}};
		String path = "";
		String pathPut = "";
		String goodExample = "{\"idShort\":\"example\",\"id\":\"example\"}";
		switch(route.getType()) {
		case "get":
			switch(route.getTag()) {
			case "Asset Administration Shell":
				path = route.getRoute().equals("/aas/{aas.idShort}/")? Constants.EXAMPLE_AAS : Constants.EXAMPLE_SUBMODEL_LIST;
				break;
			case "Asset":
				path = Constants.EXAMPLE_ASSETS;
			case "Submodel":
				path = route.getRoute().equals("/aas/{aas.idShort}/submodels/{submodel.idShort}/")? Constants.EXAMPLE_SUBMODEL : Constants.EXAMPLE_ELEMENT_LIST;
				break;
			case "Submodelelement":
				path = Constants.EXAMPLE_ELEMENT;
				goodExample = "{\"idShort\":\"example\",\"modelType\":{\"name\":\"Property\"}}";
				parameter = new String[][] {new String[] {"idShort", "example"}, new String[] {"id", "example"}, new String[] {"modelType", "{\"name\":\"Property\"}"}};
				break;
			case "Concept Description":
				path = route.getRoute().equals("/aas/{aas.idShort}/cds")? Constants.EXAMPLE_CD : Constants.EXAMPLE_CD_LIST;
				break;
			}
			good = restService.httpGet(routes.getBaseUrl()+routes.replaceIDs(route.getRoute()));
			bad = restService.httpGet(routes.getBaseUrl()+routes.replaceIDs(path));
			return new Response[] {
					new Response(bad[0], bad[1], Constants.API_RESPONSE, null),
					new Response(good[0], "successful operation", Constants.API_RESPONSE, null)
			};
		case "delete":
			switch(route.getTag()) {
			case "Asset Administration Shell":
				path = Constants.EXAMPLE_AAS;
				break;
			case "Submodel":
				path = Constants.EXAMPLE_SUBMODEL;
				break;
			case "Submodelelement":
				path = Constants.EXAMPLE_ELEMENT;
				goodExample = "{\"idShort\":\"example\",\"modelType\":{\"name\":\"Property\"}}";
				parameter = new String[][] {new String[] {"idShort", "example"}, new String[] {"id", "example"}, new String[] {"modelType", "{\"name\":\"Property\"}"}};
				break;
			case "Concept Description":
				path = Constants.EXAMPLE_CD;
				break;
			}
			restService.httpPut(routes.replaceIDs(path.replace("example", "")), goodExample, parameter);
			good = restService.httpDelete(routes.getBaseUrl()+routes.replaceIDs(path), goodExample);
			bad = restService.httpDelete(routes.getBaseUrl()+routes.replaceIDs(path), goodExample);
			return new Response[] {
					new Response(bad[0], bad[1], Constants.API_RESPONSE, null),
					new Response(good[0], good[1], Constants.API_RESPONSE, null)
			};
		case "put":
			switch(route.getTag()) {
			case "Asset Administration Shell":
				path = routes.replaceIDs(Constants.EXAMPLE_AAS);
				pathPut = routes.replaceIDs(Constants.PUT_AAS.getRoute());
				break;
			case "Asset":
				return Constants.PUT_ASSET_EXAMPLE_RESPONSE;
			case "Submodel":
				path = routes.replaceIDs(Constants.EXAMPLE_SUBMODEL);
				pathPut = routes.replaceIDs(Constants.PUT_SUBMODEL.getRoute());
				break;
			case "Submodelelement":
				path = routes.replaceIDs(Constants.EXAMPLE_ELEMENT);
				pathPut = routes.replaceIDs(Constants.PUT_ELEMENT.getRoute());
				goodExample = "{\"idShort\":\"example\",\"modelType\":{\"name\":\"Property\"}}";
				break;
			case "Concept Description":
				path = routes.replaceIDs(Constants.EXAMPLE_CD);
				pathPut = routes.replaceIDs(Constants.PUT_CONCEPT_DESCRIPTION.getRoute());
				break;
			}
			restService.httpDelete(routes.getBaseUrl() + path, goodExample);
			good = restService.httpPut(pathPut, goodExample, parameter);
			bad = restService.httpPut(pathPut, "example", new String[][] {});
			restService.httpDelete(routes.getBaseUrl() + path, goodExample);
			return new Response[] {
					new Response(bad[0], bad[1], Constants.API_RESPONSE, null),
					new Response(good[0], good[1], Constants.API_RESPONSE, null)
			};
		}
		return null;
	}
	
	public static Info generateInfo(RestService restService, Routes routes) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(restService.httpGet(routes.getBaseUrl()+routes.getAASRouteWithId())[1]);
			String description = "";
			String title = "Asset Adminstration Shell: " + routes.getAASId();
			try {
				JSONArray descriptions = (JSONArray) ((JSONObject) parser.parse(json.get("Asset").toString())).get("descriptions");
				int i = 0; 
				for(Object descriptionObj : descriptions) {
					JSONObject descriptionValue = (JSONObject) descriptionObj;
					if(i == 0 || descriptionValue.get("language").equals("EN")) {
						description = "REST server for access to the Asset Administration Shell: " + routes.getAASId() + ". The AAS belongs to the asset: " + descriptionValue.get("text");
					}
					i++;
				}
			} catch(NullPointerException desc) {
				description = "REST server for access to the Asset Administration Shell: " + routes.getAASId();
			}
			return new Info(description, "1.0", title, null, null, null);
		} catch (ParseException parse) {
			return null;
		}
	}
	
	public static Tag[] generateTags() {
		String[] tagNames = new String[] {"Asset Administration Shell", "Asset", "Submodel", "Submodelelement", "Concept Description"};
		ArrayList<Tag> tagList = new ArrayList<Tag>();
		for(String tag : tagNames) {
			tagList.add(new Tag(tag, "Operations that belong to " + tag + "s", null));
		}
		Tag[] tags = new Tag[tagList.size()];
		int i = 0;
		for(Tag tag : tagList) {
			tags[i] = tag;
			i++;
		}
		return tags;
	}
	
	public static Path[] generatePaths(RestService restService, Routes routes) {
		Path[] paths = new Path[Routes.getRoutes().length];
		int i = 0;
		for(Route route : Routes.getRoutes()) {
			String[] consumes;
			String[] produces;
			if(route.getType().equals("get")) {
				consumes = new String[] {};
				produces = new String[] {"application/json"};
			} else {
				consumes = new String[] {"application/json"};
				produces = new String[] {"text/plain"};
			}
			Request request = new Request(route.getType(), new String[] {route.getTag()}, route.getSummary(), "", null, consumes, produces, generateParameters(route), generateResponses(restService, routes, route), "false");
			paths[i] = new Path(route.getRoute(), request);
			i++;
		}
		return paths;
	}
	
	@SuppressWarnings("rawtypes")
	public static Definition[] generateDefinitions(RestService restService, Routes routes) {
		try {
			JSONParser parser = new JSONParser();
			Definition[] definitions = new Definition[6];
			JSONObject response = (JSONObject) parser.parse(restService.httpGet(routes.getBaseUrl()+routes.getAASRouteWithId())[1]);
			Property[] apiResponseProperties = new Property[] {
					new Property("resultCode", "integer", "int32", null, null, "200", null, null, null),
					new Property("type", "string", null, null, null, "application/json", null, null, null),
					new Property("message", "string", null, null, null, response.toString(), null, null, null)
			};
			definitions[0] = new Definition("ApiResponse", "object", new String[] {"resultCode", "type", "message"}, apiResponseProperties);
			String[] definitionNames = new String[] {"AssetAdministrationShell", "Asset", "Submodel", "SubmodelElement", "ConceptDescription"};
			JSONObject[] definitionExamples = new JSONObject[] {
					(JSONObject)parser.parse(response.get("AAS").toString()),
					(JSONObject) parser.parse(response.get("Asset").toString()),
					(JSONObject) parser.parse(restService.httpGet(routes.getBaseUrl()+routes.getSubmodelRouteWithId())[1]),
					(JSONObject) parser.parse(restService.httpGet(routes.getBaseUrl()+routes.getElementRouteWithId())[1]),
					(JSONObject) parser.parse(restService.httpGet(routes.getBaseUrl()+routes.getConceptDescriptionRouteWithId())[1])
			};
			for(int i = 0; i < definitionExamples.length; i++) {
				Property[] properties;
				if (definitionExamples[i].size() > 0) {
					properties = new Property[definitionExamples[i].size()];
					int j = 0;
			        for (Iterator iterator = definitionExamples[i].keySet().iterator(); iterator.hasNext();) {
			        	String key = (String) iterator.next();
			        	String value;
			        	String type;
			        	String format;
			        	try {
			        		value = (String) definitionExamples[i].get(key).toString();
			        		type = Checks.variableType(value);
			        		format = type.equals("integer")?"int64":null;
			        	} catch(NullPointerException nullPointer) {
			        		value = null;
			        		type = null;
			        		format = null;
			        	}
			        	properties[j] = new Property(key, type, format, null, null, value, null, null, null);
			        	j++;
			        }
			    } else {
			    	properties = null;
			    }
				definitions[i+1] = new Definition(definitionNames[i], "object", new String[]{"idShort"}, properties);
			}
			return definitions;
		} catch(ParseException parse) {
			return null;
		}
	}
	
	public static ExternalDocs generateExternalDocs(RestService restService, Routes routes) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(restService.httpGet(routes.getBaseUrl()+routes.getAASRouteWithId())[1]);
			String description = "";
			String url = "";
			try {
				JSONObject identification = (JSONObject)((JSONObject) parser.parse(json.get("Asset").toString())).get("identification");
				if(identification.get("type").toString().equals("IRI")) {
					url = identification.get("id").toString();
				}
				try {
					JSONArray descriptions = (JSONArray) ((JSONObject) parser.parse(json.get("Asset").toString())).get("descriptions");
					int i = 0; 
					for(Object descriptionObj : descriptions) {
						JSONObject descriptionValue = (JSONObject) descriptionObj;
						if(i == 0 || descriptionValue.get("language").equals("EN")) {
							description = "More information about " + descriptionValue.get("text");
						}
						i++;
					}
				} catch(NullPointerException desc) {
					description = "More information about " + routes.getAASId();
				}
				return new ExternalDocs(description, url);
			} catch(NullPointerException urlException) {
				return null;
			}
		} catch(ParseException parse) {
			return null;
		}
	}
}
