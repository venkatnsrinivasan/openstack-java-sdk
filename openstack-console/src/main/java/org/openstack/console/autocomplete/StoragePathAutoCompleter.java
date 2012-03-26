package org.openstack.console.autocomplete;

import java.util.List;

import org.openstack.api.storage.AccountResource;
import org.openstack.console.OpenstackCliContext;
import org.openstack.console.common.CliContext;
import org.openstack.console.common.autocomplete.SimpleArgumentAutoCompleter;
import org.openstack.model.storage.SwiftContainer;
import org.openstack.model.storage.SwiftStorageObject;

import com.google.common.collect.Lists;

public class StoragePathAutoCompleter extends SimpleArgumentAutoCompleter {

	@Override
	public List<String> doComplete(CliContext context, String prefix) throws Exception {
		List<String> strings = Lists.newArrayList();

		OpenstackCliContext osContext = (OpenstackCliContext) context;
		AccountResource client = osContext.getStorageClient();

		if (!prefix.contains("/")) {
			Iterable<SwiftContainer> items = client.get();
			for (SwiftContainer item : items) {
				strings.add(item.getName());
			}
			addSuffix(strings, "/");
			return strings;
		} else {
			String[] pathTokens = prefix.split("/");
			if (pathTokens.length == 1 || pathTokens.length == 2) {
				Iterable<SwiftStorageObject> items = client.container(pathTokens[0]).get();
				for (SwiftStorageObject item : items) {
					strings.add(item.getName());
				}

				addPrefix(strings, pathTokens[0] + "/");
				addSuffix(strings, " ");
				return strings;
			} else {
				return null;
			}
		}
	}

}