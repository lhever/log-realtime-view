package com.lhever.sc.devops.core.support.node;

import com.lhever.sc.devops.core.constant.CommonConstants;
import com.lhever.sc.devops.core.utils.CollectionUtils;
import com.lhever.sc.devops.core.utils.ObjectUtils;
import com.lhever.sc.devops.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CommonNode<ID> {
    private final static Logger log = LoggerFactory.getLogger(CommonNode.class);
    private final static String UNKNOWN = "unknown";

    public static final int IS_LEAF_TRUE_INT = 1;
    public static final int IS_LEAF_FALSE_INT = 0;

    public static final boolean IS_LEAF_FALSE = false;
    public static final boolean IS_LEAF_TRUE = true;


    protected ID id;

    protected ID parentId;

    protected Integer sort;

    protected boolean hasAuth = false;

    protected String path;

    protected List<CommonNode> children = null;

    protected String label;

    protected boolean isLeaf;

    protected boolean isRootNode;

    protected int depth;

    public CommonNode() {
    }

    public CommonNode(boolean init) {
        if (init) {
            init();
        }
    }

    protected void init() {
        id = getId();
        parentId = getParentId();
        sort = getSort();
        path = getPath();
        label = getLabel();
        isLeaf = getIsLeaf();
        isRootNode = getIsRootNode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CommonNode another = (CommonNode) o;

        return Objects.equals(id, another.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void add(CommonNode child) {
        if (child == null) {
            return;
        }

        if (CollectionUtils.isEmpty(children)) {
            this.children = new ArrayList<CommonNode>();
        }

        children.add(child);
    }


    public void add(List<? extends CommonNode> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }

        if (CollectionUtils.isEmpty(children)) {
            this.children = new ArrayList<>();
        }
        for (CommonNode node : nodes) {
            if (node == null) {
                continue;
            }
            children.add(node);
        }
    }


    public void sort() {
        if (children == null || children.size() <= 1) {
            return;
        }

        sort(children);
    }

    public static void sort(List<? extends CommonNode> nodes) {
        //???????????????????????????
        Collections.sort(nodes, (n1, n2) -> {

            Integer sort1 = n1.getSort() == null ? 0 : n1.getSort();
            Integer sort2 = n2.getSort() == null ? 0 : n2.getSort();

            return sort1.compareTo(sort2);
        });
    }

    public void sort(Comparator<? super CommonNode> c) {
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        //???????????????????????????
        Collections.sort(children, c);
    }

    public void removeRepeat() {
        if (children == null || children.size() <= 1) {
            return;
        }

        this.children = CollectionUtils.removeRepeat(children);
    }


    public void removeChildren() {
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        children.clear();
        this.children = null;
    }

    public void removeChildren(boolean setNull) {
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        children.clear();
        if (setNull) {
            this.children = null;
        }
    }

    public static List removeChildren(List<? extends CommonNode> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return nodes;
        }
        for (CommonNode node : nodes) {
            if (node == null) {
                continue;
            }
            node.removeChildren();
        }
        return nodes;
    }

    public static List removeChildren(List<? extends CommonNode> nodes, boolean setNull) {
        if (CollectionUtils.isEmpty(nodes)) {
            return nodes;
        }
        for (CommonNode node : nodes) {
            if (node == null) {
                continue;
            }
            node.removeChildren(setNull);
        }
        return nodes;
    }


    public ID getId() {
        ID id = id();
        if (id == null) {
            throw new IllegalArgumentException("node id cannot be null");
        }
        return id;
    }

    public abstract ID id();

    public void setId(ID id) {
        this.id = id;
    }

    public ID getParentId() {
        return parentId();
    }

    public abstract ID parentId();

    public void setParentId(ID parentId) {
        this.parentId = parentId;
    }

    public abstract Integer getSort();

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public abstract String getPath();

    public void setPath(String path) {
        this.path = path;
    }

    public List<CommonNode> getChildren() {
        return children;
    }

    public int childrenSize() {
        if (children == null) {
            return 0;
        }
        return children.size();
    }

    public void setChildren(List<CommonNode> children) {
        this.children = children;
    }

    public boolean getHasAuth() {
        return hasAuth;
    }

    public void setHasAuth(boolean hasAuth) {
        this.hasAuth = hasAuth;
    }

    public abstract String getLabel();

    public void setLabel(String label) {
        this.label = label;
    }

    public abstract boolean getIsLeaf();

    public void setIsLeaf(boolean leaf) {
        this.isLeaf = leaf;
    }

    public abstract boolean getIsRootNode();

    public void setIsRootNode(boolean isRootNode) {
        this.isRootNode = isRootNode;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????index,??????????????????????????????????????????
     * ?????? ????????????????????????0?????????????????????????????????1????????????????????????????????????2???????????????....
     *
     * @param parentLevel ??????????????????
     * @param index       ???????????????????????????index,??????????????????????????????????????????
     * @return
     * @author lihong10 2019/3/15 15:43
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/3/15 15:43
     * @modify by reason:{??????}
     */
    private void removeChildrenFromDepth(int parentLevel, int index) {

        if (index < 0) {
            return;
        }

        if (getIsRootNode()) {
            if ((parentLevel != -1)) { //?????????????????????????????????parentLevel == -1
                throw new IllegalArgumentException(" root node, param parentLevel == -1 required!");
            }
        } else {
            if (parentLevel < 0) { //????????????????????????????????????parentLevel >= 0
                throw new IllegalArgumentException(" sub node, param parentLevel >= 0 required!");
            }
        }

        int level = parentLevel + 1;

        if (CollectionUtils.isEmpty(children)) {
            children = null;
            return;
        }

        if (level >= index) {
            children.clear();
            children = null;
        } else {
            for (CommonNode node : children) {
                node.removeChildrenFromDepth(level, index);
            }
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????index,??????????????????????????????????????????
     * ?????? ????????????????????????0?????????????????????????????????1????????????????????????????????????2???????????????....
     *
     * @param index ???????????????????????????index,??????????????????????????????????????????
     * @return
     * @author lihong10 2019/3/15 15:43
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/3/15 15:43
     * @modify by reason:{??????}
     */
    public void removeChildrenFromDepth(int index) {
        removeChildrenFromDepth(-1, index);
    }


    public void collectPathEqDepth(int parentLevel, int index, Collection<String> paths) {
        if (paths == null) {
            throw new IllegalArgumentException("paths cannot be null");
        }

        if (index < 0) {
            return;
        }

        if (getIsRootNode()) {
            if ((parentLevel != -1)) { //?????????????????????????????????parentLevel == -1
                throw new IllegalArgumentException(" root node, param parentLevel == -1 required!");
            }
        } else {
            if (parentLevel < 0) { //????????????????????????????????????parentLevel >= 0
                throw new IllegalArgumentException(" sub node, param parentLevel >= 0 required!");
            }
        }

        int level = parentLevel + 1;

        if (CollectionUtils.isEmpty(children)) {
            children = null;
            return;
        }

        if (level == index) {
            paths.add(path);
        } else {
            for (CommonNode node : children) {
                node.collectPathEqDepth(level, index, paths);
            }
        }
    }


    public <T> void childIdsExclude(Collection<T> idSet) {
        if (idSet == null) {
            throw new IllegalArgumentException("idSet cannot be null");
        }

        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        for (CommonNode node : children) {
            idSet.add((T) node.getId());
            node.childIdsExclude(idSet);
        }
    }


    /**
     * ???????????????????????????, ???????????????, ?????????function???????????????????????????Collection?????????
     * @author lihong10 2019/7/11 14:24
     * @param function
     * @param e
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:24
     * @modify by reason:{??????}
     */
    public <E, T> void collectByFuncExclude(Collection<T> set, Function<E, T> function, Class<E> e) {
        if (set == null) {
            throw new IllegalArgumentException("idSet cannot be null");
        }

        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        for (CommonNode node : children) {
            E cast = ObjectUtils.cast(e, node);
            T value = function.apply(cast);
            set.add(value);
            node.collectByFuncExclude(set, function, e);
        }
    }

    /**
     * ???????????????????????????, ???????????????,  ?????????function???????????????????????????list?????????
     * @author lihong10 2019/7/11 14:24
     * @param function
     * @param e
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:24
     * @modify by reason:{??????}
     */
    public <E, T> List<T> collectToListByFuncExclude(Function<E, T> function, Class<E> e) {
        List<T> results = new ArrayList<>();
        collectByFuncExclude(results, function, e);
        return results;
    }

    /**
     * ???????????????????????????, ????????????,  ?????????function???????????????????????????list?????????
     * @author lihong10 2019/7/11 14:24
     * @param function
     * @param e
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:24
     * @modify by reason:{??????}
     */
    public <E, T> List<T> collectToListByFuncInclude(Function<E, T> function, Class<E> e) {
        List<T> results = new ArrayList<>();
        collectByFuncExclude(results, function, e);

        E cast = ObjectUtils.cast(e, this);
        T value = function.apply(cast);
        results.add(value);
        return results;
    }



    /**
     * ???????????????????????????, ???????????????,  ?????????function???????????????????????????set?????????
     * @author lihong10 2019/7/11 14:24
     * @param function
     * @param e
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:24
     * @modify by reason:{??????}
     */
    public <E, T> Set<T> collectToSetByFuncExclude(Function<E, T> function, Class<E> e) {
        Set<T> results = new HashSet<>();
        collectByFuncExclude(results, function, e);
        return results;
    }


    /**
     * ???????????????????????????, ????????????,  ?????????function???????????????????????????set?????????
     * @author lihong10 2019/7/11 14:24
     * @param function
     * @param e
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:24
     * @modify by reason:{??????}
     */
    public <E, T> Set<T> collectToSetByFuncInclude(Function<E, T> function, Class<E> e) {
        Set<T> results = new HashSet<>();
        collectByFuncExclude(results, function, e);

        //?????????????????????????????????
        E cast = ObjectUtils.cast(e, this);
        T value = function.apply(cast);
        results.add(value);
        return results;
    }


    /**
     * ??????????????????????????????id??????set?????????
     * @author lihong10 2019/7/11 14:07
     * @param
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:07
     * @modify by reason:{??????}
     */
    public <T> Set<T> childIdSetExclude() {
        Set<T> ids = new HashSet<>();
        childIdsExclude(ids);
        return ids;
    }

    /**
     * ??????????????????????????????id, ???????????????id??????set?????????
     * @author lihong10 2019/7/11 14:07
     * @param
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:07
     * @modify by reason:{??????}
     */
    public <T> Set<T> childIdSetInclude() {
        Set<T> ids = new HashSet<>();
        childIdsExclude(ids);
        ids.add((T)getId());
        return ids;
    }

    /**
     * ??????????????????????????????id??????list?????????
     * @author lihong10 2019/7/11 14:07
     * @param
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:07
     * @modify by reason:{??????}
     */
    public <T> List<T> childIdListExclude() {
        List<T> ids = new ArrayList<>();
        childIdsExclude(ids);
        return ids;
    }

    /**
     * ??????????????????????????????id, ???????????????id??????list?????????
     * @author lihong10 2019/7/11 14:07
     * @param
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:07
     * @modify by reason:{??????}
     */
    public <T> List<T> childIdListInclude() {
        List<T> ids = new ArrayList<>();
        childIdsExclude(ids);
        ids.add((T)getId());
        return ids;
    }


    /**
     * ?????????????????????
     *
     * @param parentDepth
     * @return
     * @author lihong10 2019/3/26 18:41
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/3/26 18:41
     * @modify by reason:{??????}
     */
    private void doCalculateDepth(int parentDepth) {
        if (getIsRootNode()) {
            if (parentDepth != -1) { //?????????????????????????????????parentDepth == -1
                throw new IllegalArgumentException(" root node, param parentDepth == -1 required!");
            }
        } else {
            if (parentDepth < 0) { //????????????????????????????????????parentDepth >= 0
                throw new IllegalArgumentException(" sub node, param parentDepth >= 0 required!");
            }
        }

        depth = parentDepth + 1;

        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (CommonNode node : children) {
            node.doCalculateDepth(depth);
        }
    }

    public void calculateDepth() {
        if (!getIsRootNode()) {
            return;
        }
        doCalculateDepth(-1);
    }

    /**
     * ???????????????????????????????????????????????????????????????
     * path??????????????????????????????????????????????????????
     *
     * @param parentPath
     * @return
     * @author lihong10 2019/3/26 18:54
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/3/26 18:54
     * @modify by reason:{??????}
     */
    private void doCalculatePath(String parentPath) {
        String tempPath = null;
        if (getIsRootNode()) {
            tempPath = CommonConstants.SLASH + id;
        } else {
            tempPath = parentPath + CommonConstants.SLASH + id;
        }

        setPath(tempPath);

        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        for (CommonNode child : children) {
            child.doCalculatePath(tempPath);
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????
     * @author lihong10 2019/7/11 14:15
     * @param
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:15
     * @modify by reason:{??????}
     */
    public void calculatePath() {
        if (!getIsRootNode()) {
            return;
        }

        doCalculatePath(null);
    }


    /**
     * ????????????????????????
     * @author lihong10 2019/7/11 14:11
     * @return
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/7/11 14:11
     * @modify by reason:{??????}
     */
    private void calcuRelativePath(String parentPath) {
        String tempPath = null;
        if (StringUtils.isNotBlank(parentPath)) {
            tempPath = parentPath + CommonConstants.SLASH + id;
        } else {
            tempPath = CommonConstants.SLASH + id;
        }

        setPath(tempPath);

        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        for (CommonNode child : children) {
            child.calcuRelativePath(tempPath);
        }
    }


    public void collectPathEqDepth(int index, Collection<String> paths) {
        collectPathEqDepth(-1, index, paths);

    }

    public Set<String> collectPathEqDepthToSet(int index) {
        Set<String> paths = new HashSet<String>();
        collectPathEqDepth(-1, index, paths);
        return paths;
    }

    public List<String> collectPathEqDepthToList(int index) {
        List<String> paths = new ArrayList<>();
        collectPathEqDepth(-1, index, paths);
        return paths;
    }


    public void setAuth(Collection<ID> auths) {
        if (auths.contains(id)) {
            propagateAuth(true);
            return;//??????????????????????????????????????????setAuth()????????????????????????
        } else {
            setHasAuth(false);
        }

        if (CollectionUtils.isEmpty(getChildren())) {
            return;
        }


        for (CommonNode child : children) {
            child.setAuth(auths);
        }
    }


    public void propagateAuth(boolean parentAuth) {
        setHasAuth(parentAuth);
        if (CollectionUtils.isEmpty(getChildren())) {
            return;
        }

        for (CommonNode child : children) {
            child.propagateAuth(parentAuth);
        }
    }

    public static <A> Set<String> splitAndCollect(List<A> nodes, Function<A, String> pathFunc) {
        //??????????????????????????????????????????????????????
        if (nodes == null) {
            return new HashSet<String>(0);
        }

        Set<String> pathIds = new HashSet<String>();
        for (A node : nodes) {
            if (node == null) {
                continue;
            }
            String path = pathFunc.apply(node);
            if (StringUtils.isBlank(path)) {
                continue;
            }
            String[] splitId = path.split(CommonConstants.SLASH);

            for (String id : splitId) {

                if (StringUtils.isNotBlank(id)) {
                    pathIds.add(id);
                }
            }
        }
        return pathIds;
    }


    public static void distinctAndSort(List<? extends CommonNode> nodes) {
        if (nodes == null) {
            return;
        }
        for (CommonNode node : nodes) {
            if (node == null) {
                continue;
            }
            //????????????????????????????
            node.removeRepeat();
            //????????????
            node.sort();
        }
    }


    public static <A, B> List<B> convertToTree(List<A> datas, Function<A, CommonNode> mapper) {
        List<CommonNode> commonNodes = datas.stream().map(item -> mapper.apply(item)).collect(Collectors.toList());
        List<B> nodes = buildTree(commonNodes);
        return nodes;
    }

    public static <A, B> List<B> convertToTree(List<A> datas, Function<A, CommonNode> mapper, Map idNodeMap) {
        List<CommonNode> commonNodes = datas.stream().map(item -> mapper.apply(item)).collect(Collectors.toList());
        return buildTree(commonNodes, idNodeMap);
    }

    public static <A, B> List<B> convertToTree(List<A> datas, List nodes, Function<A, CommonNode> mapper, Map idNodeMap) {
        List<CommonNode> commonNodes = datas.stream().map(item -> {
            CommonNode n = mapper.apply(item);
            nodes.add(n);
            return n;
        })
                .collect(Collectors.toList());
        return buildTree(commonNodes, idNodeMap);
    }


    public static <B> List<B> buildTree(List<? extends CommonNode> nodes) {
        List<CommonNode> commonNodes = doBuildTree(nodes);
        return toNode(commonNodes);
    }

    public static <B> List<B> buildTree(List<? extends CommonNode> nodes, Map idNodeMap) {
        List<CommonNode> commonNodes = doBuildTree(nodes, idNodeMap);
        return toNode(commonNodes);
    }

    public static List<CommonNode> doBuildTree(List<? extends CommonNode> nodes) {

        if (CollectionUtils.isEmpty(nodes)) {
            return new ArrayList<CommonNode>(0);
        }

        Map<Object, CommonNode> idNodeMap = new HashMap<Object, CommonNode>();

        ////////////////////////// ?????????////////////////////////////
        List<CommonNode> roots = doBuildTree(nodes, idNodeMap);

        //?????????????????????
        sort(roots);


        return roots;
    }

    public static List<CommonNode> doBuildTree(List<? extends CommonNode> nodes, Map<Object, CommonNode> idNodeMap) {
        ////////////////////////// ?????????////////////////////////////

        for (CommonNode node : nodes) {
            idNodeMap.put(node.getId(), node);
        }

        //????????????????????????
        List<CommonNode> roots = new LinkedList<>();
        for (CommonNode node : nodes) {

            if (node.getIsRootNode()) {
                roots.add(node);
                //???????????????????????????
                continue;
            }

            CommonNode parent = idNodeMap.get(node.getParentId());
            if (parent == null) {
                log.error("??????(label = {}, id = {})???????????????", node.getLabel(), node.getId());
                continue;
            }
            //???????????????????????????
            parent.add(node);
        }

        //???????????????
        distinctAndSort(nodes);

        //??????????????????
        for (CommonNode root : roots) {
            root.doCalculateDepth(-1);
        }

        return roots;
    }

    public static <B> List<B> toNode(List<CommonNode> abstractNodes) {
        if (CollectionUtils.isEmpty(abstractNodes)) {
            return new ArrayList<B>(0);
        }
        List<B> collect = new ArrayList<>(abstractNodes.size());
        for (CommonNode node : abstractNodes) {
            collect.add((B) node);
        }

//        List<A> collect = abstractNodes.stream()
//                .map(x->(A)x)
//                .collect(Collectors.toList());

        return collect;
    }

    public static <A> List<CommonNode> toCommonNode(List<A> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return new ArrayList<CommonNode>(0);
        }
        List<CommonNode> collect = new ArrayList<CommonNode>(nodes.size());
        for (A node : nodes) {
            collect.add((CommonNode) node);
        }
//        List<A> collect = abstractNodes.stream()
//                .map(x->(A)x)
//                .collect(Collectors.toList());

        return collect;
    }


    /**
     * ?????????????????????????????????,
     * ??????:
     * ??????????????????    [aa/bb/cc/dd, aa/bb, ee/ff/gg, ee, cc, cc]
     * ?????????????????????  [aa/bb, ee, cc]
     *
     * @param paths
     * @return
     * @author lihong10 2019/3/21 15:01
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/3/21 15:01
     * @modify by reason:{??????}
     */
    public static List<String> removeChildPath(List<String> paths) {
        if (CollectionUtils.isEmpty(paths)) {
            return paths;
        }

        for (int i = 0; i < paths.size(); i++) {
            for (int j = i + 1; j < paths.size(); j++) {
                String a = paths.get(i);
                String b = paths.get(j);
                if (b.startsWith(a)) {
                    paths.remove(j);
                    j--;
                } else if (a.startsWith(b)) {
                    paths.remove(i);
                    j--;
                }
            }
        }
        return paths;
    }


    /**
     * ?????????????????????paths?????? ??????paths???????????????????????????
     *
     * @param all   ????????????
     * @param paths ??????????????????
     * @return
     * @author lihong10 2019/3/21 15:44
     * @modificationHistory=========================????????????????????????????????????
     * @modify by user: {?????????} 2019/3/21 15:44
     * @modify by reason:{??????}
     */
    public static List<? extends CommonNode> getByPathStartwith(List<? extends CommonNode> all, Collection<String> paths) {
        if (CollectionUtils.isEmpty(all) || CollectionUtils.isEmpty(paths)) {
            return new ArrayList<CommonNode>(0);
        }

        List<CommonNode> filtered = all
                .stream()
                .filter(startsWithPredicate(node -> node.getPath(), paths))
                .collect(Collectors.toList());

        return filtered;
    }

    public static <A> List<A> getByPathStartwith(List<A> all, Function<A, String> pathFunc, Collection<String> paths) {
        if (CollectionUtils.isEmpty(all) || CollectionUtils.isEmpty(paths)) {
            return new ArrayList<A>(0);
        }
        List<A> filtered = all
                .stream()
                .filter(startsWithPredicate(pathFunc, paths))
                .collect(Collectors.toList());

        return filtered;
    }

    public static List<String> getPathStartwith(List<String> all, Collection<String> paths) {

        return getByPathStartwith(all, Function.identity(), paths);

    }


    public static <C> Predicate<C> startsWithPredicate(Function<C, String> pathFunc, Collection<String> paths) {
        Predicate<C> predicate = item -> {
            if (item == null) {
                return false;
            }

            String p = pathFunc.apply(item);
            if (StringUtils.isBlank(p)) {
                return false;
            }
            for (String path : paths) {
                if (p.startsWith(path)) {
                    return true;
                }
            }

            return false;
        };

        return predicate;
    }


    public static <A> List<A> getValueIn(List<A> all, Function<A, String> func, Collection<String> values) {
        if (CollectionUtils.isEmpty(all) || CollectionUtils.isEmpty(values)) {
            return new ArrayList<>(0);
        }

        List<A> results = new ArrayList();
        for (A obj : all) {
            String value = func.apply(obj);
            if (value != null && values.contains(value)) {
                results.add(obj);
            }
        }
        return results;
    }

    public static <A, B> List<A> getValueIn(List<A> data, Function<A, String> dataFunc, List<B> pathSrc, Function<B, String> pathFunc) {
        Set<String> ids = splitAndCollect(pathSrc, pathFunc);
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>(0);
        }
        return getValueIn(data, dataFunc, ids);
    }

    public static <ID> String getFullNameById(List<? extends CommonNode> nodes, ID nodeId, String joiner, boolean includeRoot) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        Map<ID, ? extends CommonNode> idNodeMap = toIdMap(nodes);
        return getFullNameById(idNodeMap, nodeId, joiner, includeRoot);
    }


    public static <ID> String getFullNameById(Map<ID, ? extends CommonNode> idNodeMap, ID nodeId, String joiner, boolean includeRoot) {
        assert idNodeMap != null : "<id, node> map is null";
        CommonNode targetNode = idNodeMap.get(nodeId);
        if (targetNode == null) {
            return null;
        }

        if (targetNode.getIsRootNode()) {
            if (includeRoot) {
                return targetNode.getLabel();
            } else {
                return null;
            }
        }

        List<String> names = new LinkedList<>();
        while (targetNode != null) {
            String label = targetNode.getLabel();
            label = StringUtils.isBlank(label) ? UNKNOWN : label;

            if (!targetNode.getIsRootNode()) {
                names.add(label);
            } else {
                if (includeRoot) {
                    names.add(label);
                }
            }
            targetNode = idNodeMap.get(targetNode.getParentId());
        }

        Collections.reverse(names);
        return StringUtils.join(names, joiner);
    }


    public static <ID> Map<ID, String> getFullNameByIds(Map<ID, ? extends CommonNode> idNodeMap, List<ID> nodeIds, String joiner, boolean includeRoot) {
        Map<ID, String> idNameMap = new HashMap<>();
        for (ID id : nodeIds) {
            String fullName = getFullNameById(idNodeMap, id, joiner, includeRoot);
            idNameMap.put(id, fullName);
        }
        return idNameMap;
    }

    public static <ID> Map<ID, String> getFullNameByNodes(Map<ID, ? extends CommonNode> idNodeMap, List<? extends CommonNode> nodes, String joiner, boolean includeRoot) {
        if (CollectionUtils.isEmpty(nodes)) {
            return new HashMap<>(0);
        }

        Map<ID, String> idNameMap = new HashMap<>();
        for (CommonNode node : nodes) {
            ID id = (ID) node.getId();
            String fullName = getFullNameById(idNodeMap, id, joiner, includeRoot);
            idNameMap.put(id, fullName);
        }
        return idNameMap;
    }

    public static <ID> Map<ID, String> getFullNameByNodes(List<? extends CommonNode> all, List<? extends CommonNode> nodes, String joiner, boolean includeRoot) {
        if (CollectionUtils.isEmpty(all) || CollectionUtils.isEmpty(nodes)) {
            return new HashMap<>(0);
        }

        Map<ID, ? extends CommonNode> idNodeMap = toIdMap(all);
        Map<ID, String> idNameMap = new HashMap<>();
        for (CommonNode node : nodes) {
            ID id = (ID) node.getId();
            String fullName = getFullNameById(idNodeMap, id, joiner, includeRoot);
            idNameMap.put(id, fullName);
        }
        return idNameMap;
    }

    public static <ID> Map<ID, String> getFullName(List<? extends CommonNode> all, String joiner, boolean includeRoot) {
        if (CollectionUtils.isEmpty(all)) {
            return new HashMap<>(0);
        }
        Map<ID, ? extends CommonNode> idNodeMap = toIdMap(all);
        Map<ID, String> idNameMap = new HashMap<>();
        for (CommonNode node : all) {
            ID id = (ID) node.getId();
            String fullName = getFullNameById(idNodeMap, id, joiner, includeRoot);
            idNameMap.put(id, fullName);
        }
        return idNameMap;
    }

    public static <ID> Map<ID, ? extends CommonNode> toIdMap(List<? extends CommonNode> nodes) {

        if (CollectionUtils.isEmpty(nodes)) {
            return new HashMap<>(0);
        }
        return toMap(item -> (ID) ObjectUtils.cast(CommonNode.class, item).getId(), nodes);
    }

    public static <A, R> Map<R, A> toMap(Function<A, R> func, List<A> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return new HashMap<>(0);
        }
        Map<R, A> idNodeMap = new HashMap<>();
        for (A node : nodes) {
            if (node == null) {
                continue;
            }
            R key = func.apply(node);
            idNodeMap.put(key, node);
        }
        return idNodeMap;
    }


    public static <ID> String getFullNameById(Function<Object, String> func, List<? extends CommonNode> nodes, ID nodeId, String joiner, boolean includeRoot) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        Map<ID, ? extends CommonNode> idNodeMap = toIdMap(nodes);
        return getFullNameById(func, idNodeMap, nodeId, joiner, includeRoot);
    }


    public static <ID> String getFullNameById(Function<Object, String> func, Map<ID, ? extends CommonNode> idNodeMap, ID nodeId, String joiner, boolean includeRoot) {
        assert idNodeMap != null : "<id, node> map is null";
        CommonNode targetNode = idNodeMap.get(nodeId);
        if (targetNode == null) {
            return null;
        }

        if (targetNode.getIsRootNode()) {
            if (includeRoot) {
                return func.apply(targetNode);
            } else {
                return null;
            }
        }

        List<String> names = new LinkedList<>();
        while (targetNode != null) {
            String label = func.apply(targetNode);
            label = StringUtils.isBlank(label) ? UNKNOWN : label;

            if (!targetNode.getIsRootNode()) {
                names.add(label);
            } else {
                if (includeRoot) {
                    names.add(label);
                }
            }
            targetNode = idNodeMap.get(targetNode.getParentId());
        }

        Collections.reverse(names);
        return StringUtils.join(names, joiner);
    }


    public static <ID> Map<ID, String> getFullNameByIds(Function<Object, String> func, Map<ID, ? extends CommonNode> idNodeMap, List<ID> nodeIds, String joiner, boolean includeRoot) {
        Map<ID, String> idNameMap = new HashMap<>();
        for (ID id : nodeIds) {
            String fullName = getFullNameById(func, idNodeMap, id, joiner, includeRoot);
            idNameMap.put(id, fullName);
        }
        return idNameMap;
    }

    public static <ID> Map<ID, String> getFullNameByNodes(Function<Object, String> func, Map<ID, ? extends CommonNode> idNodeMap, List<? extends CommonNode> nodes, String joiner, boolean includeRoot) {
        if (CollectionUtils.isEmpty(nodes)) {
            return new HashMap<>(0);
        }

        Map<ID, String> idNameMap = new HashMap<>();
        for (CommonNode node : nodes) {
            ID id = (ID) node.getId();
            String fullName = getFullNameById(func, idNodeMap, id, joiner, includeRoot);
            idNameMap.put(id, fullName);
        }
        return idNameMap;
    }

    public static <ID> Map<ID, String> getFullNameByNodes(Function<Object, String> func, List<? extends CommonNode> all, List<? extends CommonNode> nodes, String joiner, boolean includeRoot) {
        if (CollectionUtils.isEmpty(all) || CollectionUtils.isEmpty(nodes)) {
            return new HashMap<>(0);
        }

        Map<ID, ? extends CommonNode> idNodeMap = toIdMap(all);
        Map<ID, String> idNameMap = new HashMap<>();
        for (CommonNode node : nodes) {
            ID id = (ID) node.getId();
            String fullName = getFullNameById(func, idNodeMap, id, joiner, includeRoot);
            idNameMap.put(id, fullName);
        }
        return idNameMap;
    }

    public static <ID> Map<ID, String> getFullName(Function<Object, String> func, List<? extends CommonNode> all, String joiner, boolean includeRoot) {
        if (CollectionUtils.isEmpty(all)) {
            return new HashMap<>(0);
        }
        Map<ID, ? extends CommonNode> idNodeMap = toIdMap(all);
        Map<ID, String> idNameMap = new HashMap<>();
        for (CommonNode node : all) {
            ID id = (ID) node.getId();
            String fullName = getFullNameById(func, idNodeMap, id, joiner, includeRoot);
            idNameMap.put(id, fullName);
        }
        return idNameMap;
    }

}