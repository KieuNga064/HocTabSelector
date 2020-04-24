package kieunga.com.recyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import kieunga.com.model.Contact;
import kieunga.com.model.OnLoadModeListener;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Contact> contacts;
    ContactAdapter contactAdapter;
    Random random=new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adđControls();
        addEvents();
    }

    private void addEvents() {
        contactAdapter.setOnLoadMoreListener(new OnLoadModeListener() {
            @Override
            public void onLoadMore() {
                contacts.add(null);
                contactAdapter.notifyItemInserted(contacts.size()-1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contacts.remove(contacts.size()-1);
                        contactAdapter.notifyItemRemoved(contacts.size());
                        int index=contacts.size();
                        int end=index+20;
                        for(int i=index;i<end;i++)
                        {
                            Contact c=new Contact();
                            c.setName("Tên"+i);
                            String phone="098";
                            int x=random.nextInt(3);
                            if(x==1)phone="098";
                            else if(x==2)phone="094";
                            for(int p=0;p<7;p++)
                                phone+=random.nextInt(10);
                            c.setPhone(phone);
                            contacts.add(c);
                        }
                        contactAdapter.notifyDataSetChanged();
                        contactAdapter.isLoading=false;
                    }
                },5000);
            }
        });
    }

    private void adđControls() {
        recyclerView=findViewById(R.id.recyclerContacts);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));
        contacts=new ArrayList<>();
        contactAdapter=new ContactAdapter();
        recyclerView.setAdapter(contactAdapter);

        for(int i=0; i<30;i++)
        {
            Contact c=new Contact();
            c.setName("Tên"+i);
            String phone="098";
            int x=random.nextInt(3);
            if(x==1)phone="098";
            else if(x==2)phone="094";
            for(int p=0;p<7;p++)
                phone+=random.nextInt(10);
            c.setPhone(phone);
            contacts.add(c);

        }


    }

    class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private final int VIEW_TYPE_ITEM=0;
        private final int VIEW_TYPE_LOADING=1;
        OnLoadModeListener onLoadMoreListener;
        public boolean isLoading=false;
        int visibleThrehold=5;
        int lastVisibleItem;
        int totalItemCount;

        public OnLoadModeListener getOnLoadMoreListener() {
            return onLoadMoreListener;
        }

        public void setOnLoadMoreListener(OnLoadModeListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        public ContactAdapter()
        {
            final LinearLayoutManager linearLayoutManager=(LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount=linearLayoutManager.getItemCount();
                    lastVisibleItem=linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    if(isLoading&&totalItemCount<=(lastVisibleItem+visibleThrehold))
                    {
                        if(onLoadMoreListener!=null)
                            onLoadMoreListener.onLoadMore();
                        isLoading=true;
                    }
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType==VIEW_TYPE_ITEM)
            {
                View contactView= LayoutInflater.from(MainActivity.this).inflate(R.layout.contact_item,parent,false);
                return new ContactViewHolder(contactView);
            }
            if(viewType==VIEW_TYPE_LOADING)
            {
                View loadingView= LayoutInflater.from(MainActivity.this).inflate(R.layout.loadingitem,parent,false);
                return new LoadingViewHolder(loadingView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof ContactViewHolder)
            {
                Contact contact=contacts.get(position);
                ContactViewHolder contactViewHolder= (ContactViewHolder) holder;
                contactViewHolder.txtName.setText(contact.getName());
                contactViewHolder.txtPhone.setText(contact.getPhone());
            }
            else if(holder instanceof LoadingViewHolder)
            {
                LoadingViewHolder loadingViewHolder= (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return contacts==null?0:contacts.size();
        }

        @Override
        public int getItemViewType(int position) {
            return contacts.get(position)==null?VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder
    {
        public ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progressBar);
        }
    }
    static class ContactViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtName;
        public TextView txtPhone;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName=itemView.findViewById((R.id.txtName));
            txtPhone=itemView.findViewById(R.id.txtPhone);
        }
    }
}
