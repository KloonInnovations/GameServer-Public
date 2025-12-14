package io.kloon.gameserver.player.proxyinfo;

import com.google.gson.Gson;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.infra.facts.KloonDataCenter;
import io.kloon.velocity.mc.LoginPluginChannels;
import io.kloon.velocity.mc.pluginchannel.proxyinfo.LoginProxyInfo;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProxyInfoQueryListener {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyInfoQueryListener.class);

    private final Gson gson = new Gson();

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        PlayerConnection connection = event.getConnection();

        CompletableFuture<KloonProxyInfo> getProxyInfo = event.sendPluginRequest(LoginPluginChannels.REQUEST_PROXY_INFO, new byte[0]).thenApply(response -> {
            if (response == null || response.payload() == null || response.payload().length == 0) {
                LOG.warn("Didn't receive proxy info in login request for " + connection.getIdentifier());
                return KloonProxyInfo.DEFAULT;
            }

            String json = new String(response.payload());
            LoginProxyInfo loginProxyInfo = gson.fromJson(json, LoginProxyInfo.class);

            KloonDataCenter datacenter = KloonDataCenter.parse(loginProxyInfo.datacenterDbKey());
            return new KloonProxyInfo(datacenter, loginProxyInfo.allocationName());
        }).orTimeout(3, TimeUnit.SECONDS);

        Kgs.INSTANCE.getFirstConfigProcessor().runAsyncAfterLogin(connection, "fetch proxy info", (player, _) -> {
            KloonProxyInfo proxyInfo = getProxyInfo.join();
            player.setProxyInfo(proxyInfo);
            LOG.info(STR."Player \{player.getUsername()} received proxy info \{proxyInfo}");
        });
    }
}
