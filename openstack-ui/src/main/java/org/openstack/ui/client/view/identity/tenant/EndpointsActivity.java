package org.openstack.ui.client.view.identity.tenant;

import org.openstack.model.identity.Endpoint;
import org.openstack.model.identity.EndpointList;
import org.openstack.model.identity.User;
import org.openstack.ui.client.OpenStackPlace;
import org.openstack.ui.client.UI;
import org.openstack.ui.client.api.DefaultAsyncCallback;
import org.openstack.ui.client.api.OpenStackClient;
import org.openstack.ui.client.api.RefreshableDataProvider;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;

public class EndpointsActivity extends AbstractActivity implements EndpointsView.Presenter {
	
	private static final EndpointsView VIEW = new EndpointsView();
	
	private OpenStackPlace place;
	
	private RefreshableDataProvider<Endpoint> dataProvider;

	private MultiSelectionModel<Endpoint> selectionModel = new MultiSelectionModel<Endpoint>();

	private DefaultSelectionEventManager<Endpoint> selectionManager = DefaultSelectionEventManager
			.<Endpoint> createCheckboxManager(0);

	public EndpointsActivity(OpenStackPlace place) {
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		VIEW.setPresenter(this);
		panel.setWidget(VIEW);
		VIEW.grid.setSelectionModel(selectionModel, selectionManager);
		dataProvider = new RefreshableDataProvider<Endpoint>(VIEW.grid) {

			@Override
			protected void onRangeChanged(HasData<Endpoint> display) {
				OpenStackClient.IDENTITY.listEndpoints(new DefaultAsyncCallback<EndpointList>() {

					@Override
					public void onSuccess(EndpointList result) {
						updateRowCount(result.getList().size(), true);
						updateRowData(0, result.getList());

					}
				});
			}

		};
	}

	@Override
	public void refresh() {
		dataProvider.refresh();
		
	}

	@Override
	public void onCreateEndpointTemplates() {
		CreateEndpointActivity activity = new CreateEndpointActivity();
		activity.start(UI.MODAL, null);
		UI.MODAL.center();
		
	}

	@Override
	public void onDeleteEndpointTemplates() {
		for(Endpoint u : selectionModel.getSelectedSet()) {
			OpenStackClient.IDENTITY.deleteEndpoint(u.getId(), new DefaultAsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					refresh();
					
				}
			});
		}
		
	}

}