package org.eltech.ddm.associtionrules.aprioriprefixtree.model;

import org.eltech.ddm.associationrules.AssociationRulesFunctionSettings;
import org.eltech.ddm.associationrules.apriori.steps.BuildTransactionStep;
import org.eltech.ddm.associationrules.apriori.steps.Calculate1ItemSetSupportStep;
import org.eltech.ddm.associationrules.apriori.steps.CreateKItemSetCandidateStep;
import org.eltech.ddm.associationrules.apriori.steps.CreateLarge1ItemSetStep;
import org.eltech.ddm.associationrules.apriori.steps.GenerateAssosiationRuleStep;
import org.eltech.ddm.associationrules.apriori.steps.IsThereCurrenttCandidate;
import org.eltech.ddm.associationrules.apriori.steps.KLargeItemSetsCycleStep;
import org.eltech.ddm.associationrules.apriori.steps.K_1LargeItemSetsCycleStep;
import org.eltech.ddm.associationrules.apriori.steps.K_1LargeItemSetsFromCurrentCycleStep;
import org.eltech.ddm.associationrules.apriori.steps.LargeItemSetItemsCycleStep;
import org.eltech.ddm.associationrules.apriori.steps.LargeItemSetListsCycleStep;
import org.eltech.ddm.associationrules.apriori.steps.TransactionItemsCycleStep;
import org.eltech.ddm.associationrules.steps.TransactionsCycleStep;
import org.eltech.ddm.associtionrules.aprioriprefixtree.steps.CreatePrefixTrieStep;
import org.eltech.ddm.associtionrules.aprioriprefixtree.steps.GetCandidateSupportStep;
import org.eltech.ddm.associtionrules.aprioriprefixtree.steps.RemoveCandadateStep;
import org.eltech.ddm.associtionrules.aprioriprefixtree.steps.TransactionsCycleStepPrefixTrie;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.MiningAlgorithm;
import org.eltech.ddm.miningcore.algorithms.StepExecuteTimingListner;
import org.eltech.ddm.miningcore.algorithms.StepSequence;
import org.eltech.ddm.miningcore.algorithms.VectorsCycleStep;
import org.eltech.ddm.miningcore.miningfunctionsettings.EMiningFunctionSettings;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;

public class AprioriPrefixTrieAlgorithm extends MiningAlgorithm{

	public AprioriPrefixTrieAlgorithm(EMiningFunctionSettings miningSettings) throws MiningException {
		super(miningSettings);
	}

	@Override
	public EMiningModel createModel(MiningInputStream inputStream) throws MiningException {
		EMiningModel resultModel = new AprioriPrefixTrieModel((AssociationRulesFunctionSettings) miningSettings);

		return resultModel;
	}

	@Override
	protected void initSteps() throws MiningException {
		VectorsCycleStep vcs = new VectorsCycleStep(miningSettings,
				new BuildTransactionStep(miningSettings));
		vcs.addListenerExecute(new StepExecuteTimingListner());

		TransactionsCycleStep tcs = new TransactionsCycleStep(miningSettings,
				new TransactionItemsCycleStep(miningSettings,
						new Calculate1ItemSetSupportStep(miningSettings),
						new CreateLarge1ItemSetStep(miningSettings)));
		tcs.addListenerExecute(new StepExecuteTimingListner());
		
		TransactionsCycleStepPrefixTrie tcspt = new TransactionsCycleStepPrefixTrie(miningSettings, 
				new CreatePrefixTrieStep(miningSettings)); 
		
		tcspt.addListenerExecute(new StepExecuteTimingListner());
		
		StepSequence ss2 = new StepSequence(miningSettings, new K_1LargeItemSetsCycleStep(miningSettings, 
				new RemoveCandadateStep(miningSettings)),
		new K_1LargeItemSetsCycleStep(miningSettings,
				new K_1LargeItemSetsFromCurrentCycleStep(miningSettings,
						new CreateKItemSetCandidateStep(miningSettings),
						new IsThereCurrenttCandidate(miningSettings,
								new GetCandidateSupportStep(miningSettings))
				)
		));
		
		ss2.addListenerExecute(new StepExecuteTimingListner());

		StepSequence ss = new StepSequence(miningSettings, tcspt, ss2);
		
		ss.addListenerExecute(new StepExecuteTimingListner());
		
		LargeItemSetListsCycleStep lislcs = new LargeItemSetListsCycleStep(miningSettings, ss);

		lislcs.addListenerExecute(new StepExecuteTimingListner());

		LargeItemSetListsCycleStep lislcs2 = new LargeItemSetListsCycleStep(miningSettings,
				new KLargeItemSetsCycleStep(miningSettings,
						new LargeItemSetItemsCycleStep(miningSettings,
								new GenerateAssosiationRuleStep(miningSettings))));
		lislcs2.addListenerExecute(new StepExecuteTimingListner());


		steps = new StepSequence(miningSettings,
				vcs,
				tcs,
				lislcs,
				lislcs2);
		
		steps.addListenerExecute(new StepExecuteTimingListner());
		
	}

}
