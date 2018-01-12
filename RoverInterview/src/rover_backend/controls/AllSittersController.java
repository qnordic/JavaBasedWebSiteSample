package rover_backend.controls;

import rover_backend.helpers.SittersHelper;
import rover_backend.model.ReviewedSitter;

import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class AllSittersController implements Serializable {
    /**
     * Managed bean class to handle the pass through of information from database to UI layer. The class sets up the
     * review model then assigns it to a JSF DataModel type for use in the JSF UI.
     */

    // Private class members specific to setting up the DataModel
    private static final long serialVersionUID = 1L;
    private SittersHelper sitterHelper = new SittersHelper();
    private DataModel<ReviewedSitter> sitterReviewModel = new ListDataModel<>(sitterHelper.getSitterReviews());

    /**
     * Get the list of sitters reviewed and sent to the UI. This moves all rendering and work to the browser as well.
     * There is no preprossessing of the data here other than assigning it to the values for consumption by the
     * UI table.
     * @return DataModel object
     */
    public DataModel<ReviewedSitter> getSitters() {
        return sitterReviewModel;
    }
}
