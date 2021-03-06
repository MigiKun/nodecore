// VeriBlock NodeCore CLI
// Copyright 2017-2019 Xenios SEZC
// All rights reserved.
// https://www.veriblock.org
// Distributed under the MIT software license, see the accompanying
// file LICENSE or http://www.opensource.org/licenses/mit-license.php.

package nodecore.cli.commands.rpc;

import com.google.inject.Inject;
import io.grpc.StatusRuntimeException;
import nodecore.api.grpc.VeriBlockMessages;
import nodecore.cli.annotations.CommandParameterType;
import nodecore.cli.annotations.CommandSpec;
import nodecore.cli.annotations.CommandSpecParameter;
import nodecore.cli.commands.serialization.FormattableObject;
import nodecore.cli.commands.serialization.GetBlockTemplatePayload;
import nodecore.cli.contracts.Command;
import nodecore.cli.contracts.CommandContext;
import nodecore.cli.contracts.DefaultResult;
import nodecore.cli.contracts.Result;
import nodecore.cli.utilities.CommandUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandSpec(
        name = "Get Block Template",
        form = "getblocktemplate",
        description = "Returns a block template along with candidate transactions")
@CommandSpecParameter(name = "mode", required = false, type = CommandParameterType.STRING)
@CommandSpecParameter(name = "capabilities", required = false, type = CommandParameterType.STRING)
public class GetBlockTemplateCommand implements Command {
    private static final Logger _logger = LoggerFactory.getLogger(GetBlockTemplateCommand.class);

    @Inject
    public GetBlockTemplateCommand() {

    }

    @Override
    public Result execute(CommandContext context) {
        Result result = new DefaultResult();

        try {
            VeriBlockMessages.GetBlockTemplateRequest.Builder requestBuilder = VeriBlockMessages.GetBlockTemplateRequest.newBuilder();
            String mode = context.getParameter("mode");
            if (mode == null || mode.isEmpty())
                mode = "template";
            requestBuilder.setMode(mode);

            String capabilities = context.getParameter("capabilities");
            if (capabilities != null) {
                String[] flags = capabilities.split(",");
                for (String flag : flags)
                    requestBuilder.addCapabilities(flag);
            }
            VeriBlockMessages.GetBlockTemplateReply reply = context
                    .adminService()
                    .getBlockTemplate(requestBuilder.build());
            if (!reply.getSuccess()) {
                result.fail();
            } else {
                FormattableObject<GetBlockTemplatePayload> temp = new FormattableObject<>(reply.getResultsList());
                temp.success = !result.didFail();
                temp.payload = new GetBlockTemplatePayload(reply);

                context.outputObject(temp);
            }
            for (VeriBlockMessages.Result r : reply.getResultsList())
                result.addMessage(r.getCode(), r.getMessage(), r.getDetails(), r.getError());
        } catch (StatusRuntimeException e) {
            CommandUtility.handleRuntimeException(result, e, _logger);
        }

        return result;
    }
}
