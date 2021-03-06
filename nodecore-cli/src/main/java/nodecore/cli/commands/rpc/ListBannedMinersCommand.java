// VeriBlock NodeCore
// Copyright 2017-2019 Xenios SEZC
// All rights reserved.
// https://www.veriblock.org
// Distributed under the MIT software license, see the accompanying
// file LICENSE or http://www.opensource.org/licenses/mit-license.php.

package nodecore.cli.commands.rpc;

import com.google.inject.Inject;
import io.grpc.StatusRuntimeException;
import nodecore.api.grpc.VeriBlockMessages;
import nodecore.cli.annotations.CommandSpec;
import nodecore.cli.commands.serialization.FormattableObject;
import nodecore.cli.commands.serialization.ListBannedMinersPayload;
import nodecore.cli.contracts.Command;
import nodecore.cli.contracts.CommandContext;
import nodecore.cli.contracts.DefaultResult;
import nodecore.cli.contracts.Result;
import nodecore.cli.utilities.CommandUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandSpec(
        name = "List banned miners",
        form = "listbannedminers",
        description = "Returns a list of UCP clients that are currently banned")
public class ListBannedMinersCommand implements Command {
    private static final Logger _logger = LoggerFactory.getLogger(ListAllowedCommand.class);

    @Inject
    public ListBannedMinersCommand() {}

    @Override
    public Result execute(CommandContext context) {
        Result result = new DefaultResult();
        try {
            VeriBlockMessages.ListBannedMinersReply reply = context
                    .adminService()
                    .listBannedMiners(VeriBlockMessages.ListBannedMinersRequest.newBuilder().build());

            if (!reply.getSuccess()) {
                result.fail();
            } else {
                FormattableObject<ListBannedMinersPayload> temp = new FormattableObject<>(reply.getResultsList());
                temp.success = !result.didFail();
                temp.payload = new ListBannedMinersPayload(reply);

                context.outputObject(temp);
            }

            for (VeriBlockMessages.Result r : reply.getResultsList()) {
                result.addMessage(r.getCode(), r.getMessage(), r.getDetails(), r.getError());
            }
        } catch (StatusRuntimeException e) {
            CommandUtility.handleRuntimeException(result, e, _logger);
        }

        return result;
    }
}