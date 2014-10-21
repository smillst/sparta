package sparta.checkers.quals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PFPermission implements Comparable<PFPermission> {
    public static final String PARAMETERIZED_PERMISSION_REGEX = "(.*)[(](.*)[)]";
    
    public static final PFPermission ANY = new PFPermission(
            FlowPermission.ANY);
	private final FlowPermission permission;
    private final List<String> parameters; 
    
    public PFPermission(FlowPermission permission) {
        this( permission, new ArrayList<String>());     
    }

    
    public PFPermission(FlowPermission permission, List<String> parameters) {
        this.permission = permission;
        this.parameters = parameters; 
        if(parameters.isEmpty()) {
            this.parameters.add("*");
        }
        Collections.sort(this.parameters);
    }
    
    public FlowPermission getPermission() {
        return this.permission;
    }
    
    public List<String> getParameters() {
        return Collections.unmodifiableList(parameters);
    }
    
    
    @Override
    public String toString() {
        // Easy case, not parameterized 
        if (getParameters() == null || getParameters().size() == 0) {
            return getPermission().toString();
        }
        String parameterizedToString = getPermission().toString();
        if(getParameters().size()==1 && getParameters().get(0).equals("*")){
            //Don't print PERMISSION(*), just print PERMISSION
            return parameterizedToString;
        }
        parameterizedToString += "(";
        for (String param : getParameters()) {
            parameterizedToString += "\""+param + "\","; 
        }
        // Return the built string, removing the final comma, and closing it with the parenthesis
        return parameterizedToString.substring(0, parameterizedToString.length() - 1) + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result
                + ((permission == null) ? 0 : permission.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PFPermission other = (PFPermission) obj;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        if (permission != other.permission)
            return false;
        return true;
    }

    /**
     * {@inheritDoc} 
     * null first, then by FlowPermission, then by size of
     * parameters, then by String compare of sorted parameters.
     */
    @Override
    public int compareTo(PFPermission other) {
        if (other == null) {
            return 1;
        }
        if (this.permission != other.permission) {
            return this.permission.toString().compareTo(
                    other.permission.toString());
        }
        if (this.parameters.size() != other.parameters.size()) {
            return this.parameters.size() - other.parameters.size();
        }
        for (String this1 : this.parameters) {
            for (String other1 : other.parameters) {
                if (this1.compareTo(other1) != 0) {
                    return this1.compareTo(other1);
                }
            }
        }

        return 0;
    }

    // TODO: Ask where this method belongs. Static method in another class?
    static public boolean coarsePermissionExists(PFPermission target, Set<PFPermission> permissions) {
        for (PFPermission permission : permissions) {
            if (permission.equals(target) ) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSink() {
        return getPermission().isSink();
    }


    public void removeStar() {
        this.parameters.remove("*");
    }


    public void addParameters(List<String> params) {
        this.parameters.addAll(params);
    }
    
    /**
     * Takes a string of the form PERMISSION(param1, param2) and returns
     * the corresponding PFPermission object.
     * PERMISSION
     * PERMISSION("param1") 
     * @param pfpString
     * @return
     */
    public static PFPermission getPFP(String pfpString) {
        pfpString = pfpString.trim();
        List<String> formattedParams = new ArrayList<String>();
        if (pfpString.matches(PARAMETERIZED_PERMISSION_REGEX)) {
            String parametersString = pfpString.substring(
                    pfpString.indexOf('(') + 1, pfpString.indexOf(')'));
            pfpString = pfpString.substring(0, pfpString.indexOf('(')).trim();

            // Save sink parameters
            String[] sinkParameterStrings = parametersString.split(",");
            for (String param : sinkParameterStrings) {
                // Strip quotes and add to parameter list
                param = param.replaceAll("\"", "").trim();
                formattedParams.add(param);
            }
        }
        PFPermission sinkPFP = new PFPermission(
                FlowPermission.valueOf(pfpString), formattedParams);
        return sinkPFP;
    }
}
