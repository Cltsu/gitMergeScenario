package nju.merge.entity;

import java.util.List;

import static nju.merge.utils.StringUtils.concat;

public class TokenConflict {
    public List<String> prefix;
    public List<String> suffix;
    public List<String> aRegion;
    public List<String> oRegion;
    public List<String> bRegion;

    public List<String> resolution;
    public ResolutionLabel label;

    public List<String> a;
    public List<String> b;
    public List<String> o;

    public TokenConflict(List<String> prefix, List<String> suffix, List<String> aRegion, List<String> oRegion, List<String> bRegion, List<String> resolution) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.aRegion = aRegion;
        this.oRegion = oRegion;
        this.bRegion = bRegion;
        this.resolution = resolution;

        this.a = concat(prefix, suffix, aRegion);
        this.b = concat(prefix, suffix, bRegion);
        this.o = concat(prefix, suffix, oRegion);
    }


    @Override
    public String toString(){
        String ret = a.toString() + "\n" + o.toString() + "\n" + b.toString() + "\n";
        return ret;
    }
}
