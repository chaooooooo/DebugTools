package chao.app.ami.plugin.plugins.logcat;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import chao.app.ami.plugin.AmiPluginFragment;
import chao.app.ami.plugin.AmiPluginManager;
import chao.app.debug.R;
import java.util.ArrayList;

/**
 * @author qinchao
 * @since 2018/9/4
 */
public class LogcatFragment extends AmiPluginFragment implements View.OnClickListener {


    private RecyclerView logcatView;

    private LogcatAdapter mAdapter;


    private LogcatManager logcatManager;

    private LogcatSettings logcatSettings;


    private ArrayList<LogcatLine> caches = new ArrayList<>();

    private View clearView;

    private ToggleButton pauseView;

    public LogcatFragment() {
        AmiPluginManager pluginManager = AmiPluginManager.getInstance();
        LogcatPlugin logcatPlugin = (LogcatPlugin) pluginManager.getPlugin(LogcatPlugin.class);
        logcatManager = logcatPlugin.getLogcatManager();
        logcatSettings = logcatPlugin.getLogcatSettings();
    }

    @Override
    public void setupView(View layout) {
        super.setupView(layout);


        logcatView = findView(R.id.logcat_list);
        logcatView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mAdapter = new LogcatAdapter();
        logcatView.setAdapter(mAdapter);
        logcatView.setLayoutManager(new LinearLayoutManager(getContext()));

        clearView = findView(R.id.ami_plugin_logcat_settings_clear);
        pauseView = findView(R.id.ami_plugin_logcat_settings_pause);
        clearView.setOnClickListener(this);
        pauseView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                logcatSettings.setPause(isChecked);
            }
        });
    }

    public void notifyDataSetCleared() {
        caches.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(ArrayList<LogcatLine> logCaches, int pos, int length) {
        if (mAdapter == null) {
            return;
        }
        if (logCaches == null || length == 0) {
            return;
        }
        caches.clear();
        caches.addAll(logCaches);
        if (logCaches.size() == 0) {
            mAdapter.notifyDataSetChanged();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v == clearView) {
            logcatManager.clear();
        }
    }

    private class LogcatAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.ami_plugin_logcat_item_layout, parent, false);
            return new RecyclerView.ViewHolder(itemView) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView logText = (TextView) holder.itemView.findViewById(R.id.log_text);
            LogcatLine logcat = caches.get(position);
            String log = logcat.getLog();
            logText.setText(log);
            logText.setTextColor(logcat.getLevel().getColor());
        }

        @Override
        public int getItemCount() {
            return caches.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }
    }

    @Override
    public int getLayoutID() {
        return R.layout.ami_plugin_logcat_fragment;
    }
}
