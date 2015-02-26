package org.eclipse.ecf.mgmt.identity.host;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IIDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.identity.IDMTO;
import org.eclipse.ecf.mgmt.identity.IIdentityFactoryManager;
import org.eclipse.ecf.mgmt.identity.NamespaceMTO;

public class IdentityFactoryManager extends AbstractManager implements IIdentityFactoryManager {

	private IIDFactory idFactory;

	void bindIDFactory(IIDFactory idFactory) {
		this.idFactory = idFactory;
	}

	void unbindIDFactory(IIDFactory idFactory) {
		this.idFactory = null;
	}

	@SuppressWarnings("rawtypes")
	public static final String[][] convertClassArrayToNameArray(Class[][] clazzes) {
		String[][] results = new String[clazzes.length][];
		for (int i = 0; i < clazzes.length; i++) {
			results[i] = new String[clazzes[i].length];
			for (int j = 0; j < clazzes[i].length; i++)
				results[i][j] = clazzes[i][j].getName();
		}
		return results;
	}

	public static NamespaceMTO createNamespaceMTO(Namespace ns) {
		return new NamespaceMTO(ns.getName(), ns.getDescription(), ns.getScheme(), ns.getSupportedSchemes(),
				convertClassArrayToNameArray(ns.getSupportedParameterTypes()));
	}

	public static IDMTO createIDMTO(ID id) {
		return new IDMTO(createNamespaceMTO(id.getNamespace()), id.getName(), id.toExternalForm());
	}

	@Override
	public NamespaceMTO[] getNamespaces() {
		@SuppressWarnings("rawtypes")
		List namespaces = idFactory.getNamespaces();
		List<NamespaceMTO> results = new ArrayList<NamespaceMTO>();
		for (@SuppressWarnings("rawtypes")
		Iterator i = namespaces.iterator(); i.hasNext();)
			results.add(createNamespaceMTO((Namespace) i.next()));
		return results.toArray(new NamespaceMTO[results.size()]);
	}

	@Override
	public NamespaceMTO getNamespace(String name) {
		Namespace ns = idFactory.getNamespaceByName(name);
		return (ns == null) ? null : createNamespaceMTO(ns);
	}

	IDMTO create0(ID id) {
		try {
			return createIDMTO(id);
		} catch (org.eclipse.ecf.core.identity.IDCreateException e) {
			throw new org.eclipse.ecf.mgmt.identity.IDCreateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public IDMTO createID(String namespaceName, Object[] args) {
		Namespace ns = idFactory.getNamespaceByName(namespaceName);
		if (ns == null)
			return null;
		return create0(idFactory.createID(ns, args));
	}

	@Override
	public IDMTO createStringID(String id) {
		return create0(idFactory.createStringID(id));
	}

	@Override
	public IDMTO createLongID(long id) {
		return create0(idFactory.createLongID(id));
	}

	@Override
	public IDMTO createGUID(int byteLength) {
		return create0(idFactory.createGUID(byteLength));
	}

	@Override
	public IDMTO createGUID() {
		return create0(idFactory.createGUID());
	}

	@Override
	public IDMTO createURIID(URI uri) {
		return create0(idFactory.createID(URIID.class.getName(), new Object[] { uri }));
	}

}