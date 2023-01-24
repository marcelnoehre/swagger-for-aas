package swagger;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import org.junit.Test;

import requests.RestService;
import swagger2java.api.AssetAdministrationShellApi;
import swagger2java.api.AssetApi;
import swagger2java.api.ConceptDescriptionApi;
import swagger2java.api.SubmodelApi;
import swagger2java.api.SubmodelelementApi;
import utils.Compare;
import utils.Routes;

public class ResponsesCompleteTest {
    private final static AssetAdministrationShellApi AAS =
            new AssetAdministrationShellApi();
    private final static AssetApi ASSET =
            new AssetApi();
    private final static SubmodelApi SUBMODEL =
            new SubmodelApi();
    private final static SubmodelelementApi ELEMENT =
            new SubmodelelementApi();
    private final static ConceptDescriptionApi CD =
            new ConceptDescriptionApi();
    private final static RestService restService =
            new RestService();
    private final static Routes routes = new Routes(
            restService, "http://localhost:1111/", "Festo_3S7PM0CP4BD");
    private final static String[] models = new String[] {
            "GET_AAS", "GET_SUBMODEL_LIST", "GET_ASSET", "GET_SUBMODEL",
            "GET_ELEMENT_LIST", "GET_ELEMENT", "GET_CD_LIST", "GET_CD"
    };

    @Test
    public void testResponses() {
        ArrayList<String> checkedModels = new ArrayList<String>();
        ArrayList<String> failedModels = new ArrayList<String>();
        try {
            if(Compare.compareAASResponse(AAS.aasAasIdShortGet(routes.getAASId()), restService.httpGet(routes.getBaseUrl() + routes.getAASRouteWithId())[1])) {
                checkedModels.add("GET_AAS");
            } else {
                failedModels.add("GET_AAS");
            }
        } catch (Exception e) { }
        try {
            if(Compare.compareSubmodelListResponse(AAS.aasAasIdShortSubmodelsGet(routes.getAASId()), restService.httpGet(routes.getBaseUrl() + routes.getSubmodelListRouteWithId())[1])) {
                checkedModels.add("GET_SUBMODEL_LIST");
            } else {
                failedModels.add("GET_SUBMODEL_LIST");
            }
        } catch (Exception e) { }
        try {
            if(Compare.compareAssetsResponse(ASSET.assetsAasIdShortGet(routes.getAASId()), restService.httpGet(routes.getBaseUrl() + routes.getAssetRouteWithId())[1])) {
                checkedModels.add("GET_ASSET");
            } else {
                failedModels.add("GET_ASSET");
            }
        } catch (Exception e) { }
        try {
            if(Compare.compareSubmodelResponse(SUBMODEL.aasAasIdShortSubmodelsSubmodelIdShortGet(routes.getAASId(), routes.getSubmodelId()), restService.httpGet(routes.getBaseUrl() + routes.getSubmodelRouteWithId())[1])) {
                checkedModels.add("GET_SUBMODEL");
            } else {
                failedModels.add("GET_SUBMODEL");
            }
        } catch (Exception e) { }
        try {
            if(Compare.compareElementListResponse(SUBMODEL.aasAasIdShortSubmodelsSubmodelIdShortTableGet(routes.getAASId(), routes.getSubmodelId()),restService.httpGet(routes.getBaseUrl() + routes.getElementListRouteWithId())[1])) {
                checkedModels.add("GET_ELEMENT_LIST");
            } else {
                failedModels.add("GET_ELEMENT_LIST");
            }
        } catch (Exception e) { }
        try {
            if(Compare.compareElementResponse(ELEMENT.aasAasIdShortSubmodelsSubmodelIdShortElementsElementIdShortGet(routes.getAASId(), routes.getSubmodelId(),routes.getElementId()), restService.httpGet(routes.getBaseUrl() + routes.getElementRouteWithId())[1])) {
                checkedModels.add("GET_ELEMENT");
            } else {
                failedModels.add("GET_ELEMENT");
            }
        } catch (Exception e) { }
        try {
            if(Compare.compareCDListResponse(CD.aasAasIdShortCdsGet(routes.getAASId()), restService.httpGet(routes.getBaseUrl() + routes.getConceptDescriptionListRouteWithId())[1])) {
                checkedModels.add("GET_CD_LIST");
            } else {
                failedModels.add("GET_CD_LIST");
            }
        } catch (Exception e) { }
        try {
            if(Compare.compareCDResponse(CD.aasAasIdShortCdsCdIdShortGet(routes.getAASId(), routes.getCdId()), restService.httpGet(routes.getBaseUrl() + routes.getConceptDescriptionRouteWithId())[1])) {
                checkedModels.add("GET_CD");
            } else {
                failedModels.add("GET_CD");
            }
        } catch (Exception e) { }
        failedModels.forEach(System.err::println);
        for (String model : models) {
            assertTrue(checkedModels.contains(model));
        }
    }
}