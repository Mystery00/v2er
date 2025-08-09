package me.ghui.v2er.module.node;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.NodeInfo;
import me.ghui.v2er.network.bean.NodeTopicInfo;
import me.ghui.v2er.network.bean.SimpleInfo;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 27/05/2017.
 */

public class NodeTopicPresenter implements NodeTopicContract.IPresenter {

    private NodeTopicContract.IView mView;
    private NodeTopicInfo mTopicInfo;
    private int mPage = 1;

    @Override
    public int getPage() {
        return mPage;
    }

    public NodeTopicPresenter(NodeTopicContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        mView.showLoading();
        APIService.get().nodeInfo(mView.nodeName())
                .compose(mView.rx(null))
                .subscribe(new GeneralConsumer<NodeInfo>() {
                    @Override
                    public void onConsume(NodeInfo nodeInfo) {
                        loadData(mView.initPage());
                        if (nodeInfo.isValid()) {
                            mView.fillHeaderView(nodeInfo);
                        } else {
                            mView.toast("加载节点信息失败");
                        }
                    }
                });
    }

    @Override
    public void loadData(int page) {
        APIService.get().nodesInfo(mView.nodeName(), page)
                .compose(mView.rx(page))
                .subscribe(new GeneralConsumer<NodeTopicInfo>() {
                    @Override
                    public void onConsume(NodeTopicInfo nodesInfo) {
                        mPage = page;
                        mView.fillListView(nodesInfo, page > 1 && mView.initPage() == 1);
                    }
                });
    }

    @Override
    public void starNode(String url) {
        if (UserUtils.notLoginAndProcessToLogin(false, mView.getContext())) return;
        APIService.get().starNode(url)
                .compose(mView.rx(null))
                .subscribe(new GeneralConsumer<SimpleInfo>(mView) {
                    @Override
                    public void onConsume(SimpleInfo simpleInfo) {
                        boolean forStar = url.contains("/favorite/");
                        if (forStar) {
                            mView.afterStarNode();
                        } else {
                            mView.afterUnStarNode();
                        }
                    }
                });
    }

    @Override
    public void ignoreNode(String url) {
        if (UserUtils.notLoginAndProcessToLogin(false, mView.getContext())) return;
        APIService.get().ignoreNode(url)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<SimpleInfo>(mView) {
                    @Override
                    public void onConsume(SimpleInfo simpleInfo) {
                        boolean forIgnore = url.contains("/ignore/");
                        if (forIgnore) {
                            mView.afterIgnoreNode();
                        } else {
                            mView.afterUnIgnoreNode();
                        }
                    }
                });
    }
}
